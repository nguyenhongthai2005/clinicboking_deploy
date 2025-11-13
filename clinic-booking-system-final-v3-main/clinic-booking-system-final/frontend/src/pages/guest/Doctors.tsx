import { useEffect, useMemo, useState } from 'react';
import { Alert, Badge, Card, Col, Container, Form, Row, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { fetchDoctorsAll, type Doctor } from '../../api/doctor';
import { fetchSpecialties, type Specialty } from '../../api/specialty';
import '../../styles/guest-doctors.css';

export default function Doctors() {
  const navigate = useNavigate();
  const [expandedDoctorId, setExpandedDoctorId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');

  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [specialties, setSpecialties] = useState<Specialty[]>([]);

  const [selectedSpecialty, setSelectedSpecialty] = useState<number | 'all'>('all');
  const [query, setQuery] = useState('');

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError('');

    Promise.allSettled([fetchDoctorsAll(), fetchSpecialties()])
      .then((results) => {
        if (!alive) return;

        const [doctorRes, specialtyRes] = results;

        if (doctorRes.status === 'fulfilled') {
          setDoctors(doctorRes.value ?? []);
        } else {
          const message =
            (doctorRes.reason && doctorRes.reason.message) || 'Không thể tải danh sách bác sĩ';
          setError(message);
        }

        if (specialtyRes.status === 'fulfilled') {
          setSpecialties(specialtyRes.value ?? []);
        }
      })
      .finally(() => {
        if (alive) setLoading(false);
      });

    return () => {
      alive = false;
    };
  }, []);

  const normalizedQuery = query.trim().toLowerCase();

  const selectedSpecialtyInfo = useMemo(() => {
    if (selectedSpecialty === 'all') return null;
    return specialties.find((s) => s.id === selectedSpecialty) ?? null;
  }, [selectedSpecialty, specialties]);

  const filteredDoctors = useMemo(() => {
    return doctors.filter((doctor) => {
      const matchesSpecialty =
        selectedSpecialty === 'all' ||
        doctor.specialtyId === Number(selectedSpecialty) ||
        (doctor.specialtyName &&
          selectedSpecialtyInfo?.name &&
          doctor.specialtyName.toLowerCase() === selectedSpecialtyInfo.name.toLowerCase());
      const matchesQuery =
        normalizedQuery.length === 0 ||
        doctor.fullName?.toLowerCase().includes(normalizedQuery) ||
        doctor.specialtyName?.toLowerCase().includes(normalizedQuery);
      return matchesSpecialty && matchesQuery;
    });
  }, [doctors, normalizedQuery, selectedSpecialty, selectedSpecialtyInfo]);

  const selectedSpecialtyName =
    selectedSpecialty === 'all'
      ? 'Tất cả chuyên khoa'
      : specialties.find((s) => s.id === selectedSpecialty)?.name ?? 'Chuyên khoa';

  return (
    <div className="guest-doctors-page py-5">
      <Container>
        <Row className="g-4">
          <Col lg={9} md={12}>
            <div className="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
              <div>
                <h2 className="fw-bold mb-0">Danh sách bác sĩ</h2>
                <div className="text-muted small">
                  {filteredDoctors.length} bác sĩ · {selectedSpecialtyName}
                </div>
              </div>
            </div>

            {loading && (
              <div className="d-flex align-items-center gap-2">
                <Spinner animation="border" size="sm" />
                <span>Đang tải danh sách bác sĩ...</span>
              </div>
            )}

            {!loading && error && (
              <Alert variant="danger">
                {error}
                <div className="mt-1 small">
                  Vui lòng đăng nhập với quyền phù hợp hoặc thử lại sau.
                </div>
              </Alert>
            )}

            {!loading && !error && filteredDoctors.length === 0 && (
              <Alert variant="info">Không tìm thấy bác sĩ phù hợp với tiêu chí.</Alert>
            )}

            <div className="vstack gap-3">
              {filteredDoctors.map((doctor) => (
                <Card key={doctor.id} className="doctor-card border-0">
                  <div className="doctor-card__main">
                    <div className="doctor-card__image">
                      <div className="doctor-card__image-placeholder">Ảnh bác sĩ</div>
                    </div>
                    <div className="doctor-card__content">
                      <div className="doctor-card__header">
                        <div>
                          <div className="doctor-card__title">{doctor.fullName}</div>
                          {doctor.degree && (
                            <div className="doctor-card__subtitle text-muted small">
                              {doctor.degree}
                            </div>
                          )}
                        </div>
                        <div className="doctor-card__specialty-row">
                          {doctor.specialtyName && (
                            <div className="doctor-card__specialty">{doctor.specialtyName}</div>
                          )}
                          <div className="doctor-card__button-group">
                            <button
                              type="button"
                              className="btn btn-outline-secondary btn-sm rounded-pill"
                              onClick={() =>
                                setExpandedDoctorId((prev) =>
                                  prev === doctor.id ? null : Number(doctor.id)
                                )
                              }
                            >
                              {expandedDoctorId === doctor.id ? 'Thu gọn' : 'Xem thông tin'}
                            </button>
                            <button
                              type="button"
                              className="btn btn-primary btn-sm rounded-pill"
                              onClick={() => navigate(`/appointment?doctorId=${doctor.id}`)}
                            >
                              <i className="bi bi-calendar3" /> Đặt lịch
                            </button>
                          </div>
                        </div>
                      </div>

                      <div className="doctor-card__meta">
                        <div>
                          <strong>Email:</strong> {doctor.email ?? '—'}
                        </div>
                        <div>
                          <strong>Điện thoại:</strong> {doctor.phoneNumber ?? '—'}
                        </div>
                      </div>

                      <div className="doctor-card__footer">
                        <span className="text-muted small">
                          Thời gian khám sẽ cập nhật sau
                        </span>
                      </div>
                    </div>
                  </div>

                  {expandedDoctorId === doctor.id && (
                    <div className="doctor-card__extra">
                      <div className="doctor-card__extra-item">
                        <span>Chuyên khoa</span>
                        <strong>{doctor.specialtyName ?? '—'}</strong>
                      </div>
                      <div className="doctor-card__extra-item">
                        <span>Kinh nghiệm</span>
                        <strong>{doctor.experience ?? '—'}</strong>
                      </div>
                      {doctor.description && (
                        <div className="doctor-card__extra-item">
                          <span>Mô tả</span>
                          <strong>{doctor.description}</strong>
                        </div>
                      )}
                    </div>
                  )}
                </Card>
              ))}
            </div>
          </Col>

          <Col lg={3} md={12}>
            <Card className="shadow-sm border-0 sticky-top" style={{ top: '6rem' }}>
              <Card.Body>
                <h5 className="fw-bold mb-3">Tìm kiếm bác sĩ</h5>
                <Form.Control
                  type="search"
                  placeholder="Nhập tên bác sĩ..."
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  className="mb-4"
                />

                <h6 className="text-uppercase text-muted small fw-semibold mb-3">Chuyên khoa</h6>
                <div className="vstack gap-2 filter-specialties">
                  <Form.Check
                    type="radio"
                    name="specialty"
                    id="specialty-all"
                    label="Tất cả"
                    checked={selectedSpecialty === 'all'}
                    onChange={() => setSelectedSpecialty('all')}
                  />
                  {specialties.map((sp) => (
                    <Form.Check
                      key={sp.id}
                      type="radio"
                      name="specialty"
                      id={`specialty-${sp.id}`}
                      label={sp.name}
                      checked={selectedSpecialty === sp.id}
                      onChange={() => setSelectedSpecialty(sp.id)}
                    />
                  ))}
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

