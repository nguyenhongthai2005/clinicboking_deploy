import { useEffect, useMemo, useState } from "react";
import { Button, Col, Form, Modal, Row, Spinner, Card } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { http } from "../../api/http";
import { fetchSpecialties, type Specialty } from "../../api/specialty";

type Gender = "Male" | "Female" | "Other";
type UserType = "Doctor" | "Receptionist";

function getErrMsg(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const msg = (err.response?.data as any)?.message;
    return msg ?? err.message ?? "Request failed";
  }
  if (err instanceof Error) return err.message;
  try { return JSON.stringify(err); } catch { return "Unknown error"; }
}

export default function AdminCreateUser() {
  const navigate = useNavigate();
  const location = useLocation();
  const fromPath = (location.state as any)?.from as string | undefined;
  const defaultUserType = ((location.state as any)?.defaultUserType as UserType) || "Receptionist";

  // Specialties (for Doctor only)
  const [specialties, setSpecialties] = useState<Specialty[]>([]);
  const [loadingSpecs, setLoadingSpecs] = useState(false);
  const [error, setError] = useState("");

  // Common form
  const [userType, setUserType] = useState<UserType>(defaultUserType);
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [gender, setGender] = useState<Gender>("Other");

  // Doctor-only form
  const [specialtyId, setSpecialtyId] = useState<number>(0);
  const [degree, setDegree] = useState("");
  const [description, setDescription] = useState("");
  const [experience, setExperience] = useState("");

  const [saving, setSaving] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");

  useEffect(() => {
    if (userType === "Doctor") {
      setLoadingSpecs(true);
      fetchSpecialties()
        .then((sp) => setSpecialties(sp))
        .catch((err) => setError(getErrMsg(err)))
        .finally(() => setLoadingSpecs(false));
    }
  }, [userType]);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!fullName.trim() || !email || !password) return;
    const payload: any = {
      fullName: fullName.trim(),
      email,
      phoneNumber,
      password,
      gender,
      userType,
    };

    if (userType === "Doctor") {
      const sp = specialties.find((s) => s.id === specialtyId);
      if (!sp) {
        setError("Please select a specialty for doctor");
        return;
      }
      payload.specialization = sp.name;
      if (degree) payload.degree = degree;
      if (description) payload.description = description;
      if (experience) payload.experience = experience;
    }

    setSaving(true);
    try {
      await http.post("/admin/create-user", payload);
      setSuccessMsg(`${userType} created successfully!`);
      setShowSuccess(true);
    } catch (err) {
      setError(getErrMsg(err));
    } finally {
      setSaving(false);
    }
  };

  const onCloseSuccess = () => {
    setShowSuccess(false);
    if (fromPath) {
      navigate(fromPath, { replace: true });
    } else {
      navigate(-1);
    }
  };

  return (
    <>
      <Row className="align-items-center mb-3">
        <Col>
          <div className="d-flex align-items-center justify-content-between">
            <h5 className="mb-0">Create User</h5>
          </div>
        </Col>
      </Row>

      {error && (
        <Row className="mb-3">
          <Col>
            <Card body className="border-danger-subtle text-danger">{error}</Card>
          </Col>
        </Row>
      )}

      <Form onSubmit={onSubmit}>
        <Row className="g-3">
          <Col md={6}>
            <Form.Group controlId="uType">
              <Form.Label>User type</Form.Label>
              <Form.Select value={userType} onChange={(e) => setUserType(e.target.value as UserType)}>
                <option value="Doctor">Doctor</option>
                <option value="Receptionist">Receptionist</option>
              </Form.Select>
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group controlId="uGender">
              <Form.Label>Gender</Form.Label>
              <Form.Select value={gender} onChange={(e) => setGender(e.target.value as Gender)}>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </Form.Select>
            </Form.Group>
          </Col>

          <Col md={12}>
            <Form.Group controlId="uFullName">
              <Form.Label>Full name</Form.Label>
              <Form.Control value={fullName} onChange={(e) => setFullName(e.target.value)} required />
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group controlId="uEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group controlId="uPhone">
              <Form.Label>Phone number</Form.Label>
              <Form.Control value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group controlId="uPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </Form.Group>
          </Col>

          {userType === "Doctor" && (
            <>
              <Col md={6}>
                <Form.Group controlId="uSpecialty">
                  <Form.Label>Specialty</Form.Label>
                  <Form.Select
                    value={specialtyId || 0}
                    onChange={(e) => setSpecialtyId(Number(e.target.value))}
                    disabled={loadingSpecs}
                    required
                  >
                    <option value={0} disabled>Select specialty</option>
                    {specialties.map((s) => (
                      <option key={s.id} value={s.id}>{s.name}</option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group controlId="uDegree">
                  <Form.Label>Degree</Form.Label>
                  <Form.Control value={degree} onChange={(e) => setDegree(e.target.value)} />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group controlId="uExperience">
                  <Form.Label>Experience</Form.Label>
                  <Form.Control value={experience} onChange={(e) => setExperience(e.target.value)} />
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group controlId="uDescription">
                  <Form.Label>Description</Form.Label>
                  <Form.Control as="textarea" rows={3} value={description} onChange={(e) => setDescription(e.target.value)} />
                </Form.Group>
              </Col>
            </>
          )}
        </Row>

        <div className="d-flex justify-content-end gap-2 mt-4">
          <Button variant="outline-secondary" onClick={() => (fromPath ? navigate(fromPath, { replace: true }) : navigate(-1))} disabled={saving}>Cancel</Button>
          <Button type="submit" disabled={saving || !fullName || !email || !password || (userType === "Doctor" && !specialtyId)}>
            {saving ? "Creating..." : "Create"}
          </Button>
        </div>
      </Form>

      <Modal show={showSuccess} onHide={onCloseSuccess} centered>
        <Modal.Header closeButton>
          <Modal.Title>Success</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {successMsg}
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={onCloseSuccess}>Close</Button>
        </Modal.Footer>
      </Modal>
    </>
  );
}


