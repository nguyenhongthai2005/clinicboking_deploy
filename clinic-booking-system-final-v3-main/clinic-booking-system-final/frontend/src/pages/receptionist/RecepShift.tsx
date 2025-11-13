import { useEffect, useState } from "react";
import { Button, Card, Form, Spinner } from "react-bootstrap";
import { http } from "../../api/http";
import { fetchDoctorsAll, type Doctor } from "../../api/doctor";
import { fetchByShift } from "../../api/appointment";
import type { Appointment } from "../../api/appointment";
import "../../styles/shift.css";

type ShiftType = "MORNING" | "AFTERNOON" | "EVENING" | string;

type ShiftItem = {
  id: number;
  date: string; // yyyy-mm-dd
  shift: ShiftType;
  doctorId: number;
  doctorName: string;
  specialtyName?: string;
};

function toISODateInput(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export default function RecepShift() {
  const [date, setDate] = useState<string>(() => toISODateInput(new Date()));
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [shifts, setShifts] = useState<ShiftItem[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState<string>("");
  const [shiftAppointments, setShiftAppointments] = useState<Record<number, Appointment[]>>({});

  async function loadAllByDate(targetDate: string) {
    setLoading(true);
    setError("");
    try {
      const res = await http.get(`/shifts/by-date?date=${encodeURIComponent(targetDate)}`);
      const data = Array.isArray(res.data?.data)
        ? res.data.data
        : Array.isArray(res.data)
        ? res.data
        : [];
      const normalized = (data || []).map(normalizeShiftItem);
      setShifts(normalized);
      // Load appointments for all shifts
      await loadAppointmentsForShifts(normalized);
    } catch (e: any) {
      setError(e?.message || "Failed to load shifts");
      setShifts([]);
    } finally {
      setLoading(false);
    }
  }

  async function loadByDoctor(doctorId: number, targetDate: string) {
    setLoading(true);
    setError("");
    try {
      const res = await http.get(`/shifts/by-doctor/${doctorId}?date=${encodeURIComponent(targetDate)}`);
      const data = Array.isArray(res.data?.data)
        ? res.data.data
        : Array.isArray(res.data)
        ? res.data
        : [];
      const normalized = (data || []).map(normalizeShiftItem);
      setShifts(normalized);
      // Load appointments for all shifts
      await loadAppointmentsForShifts(normalized);
    } catch (e: any) {
      setError(e?.message || "Failed to load shifts by doctor");
      setShifts([]);
    } finally {
      setLoading(false);
    }
  }

  async function loadAppointmentsForShifts(shiftsList: ShiftItem[]) {
    const appointmentsMap: Record<number, Appointment[]> = {};
    try {
      const results = await Promise.all(
        shiftsList.map(async (shift) => {
          try {
            const res = await fetchByShift(shift.id);
            return { shiftId: shift.id, appointments: res.ok && res.data ? res.data : [] };
          } catch {
            return { shiftId: shift.id, appointments: [] };
          }
        })
      );
      results.forEach(({ shiftId, appointments }) => {
        appointmentsMap[shiftId] = appointments;
      });
      setShiftAppointments(appointmentsMap);
    } catch (e: any) {
      console.error("Failed to load appointments:", e);
    }
  }

  function normalizeShiftItem(raw: any): ShiftItem {
    return {
      id: Number(raw.id),
      date: raw.date || raw.shiftDate || "",
      shift: raw.shift || raw.shiftType || raw.type || "",
      doctorId: Number(raw.doctorId ?? raw.doctor?.id ?? 0),
      doctorName: raw.doctorName || raw.doctor?.fullName || raw.doctor?.name || "",
      specialtyName: raw.specialtyName || raw.specialty?.name || raw.doctor?.specialtyName || null,
    } as ShiftItem;
  }

  // Load doctors and initial data
  useEffect(() => {
    fetchDoctorsAll().then(setDoctors).catch(() => {});
    loadAllByDate(date);
  }, []);

  // Auto-filter when date changes
  useEffect(() => {
    if (selectedDoctorId) {
      loadByDoctor(Number(selectedDoctorId), date);
    } else {
      loadAllByDate(date);
    }
  }, [date]);

  // Auto-filter when doctor selection changes
  useEffect(() => {
    if (selectedDoctorId) {
      loadByDoctor(Number(selectedDoctorId), date);
    } else {
      loadAllByDate(date);
    }
  }, [selectedDoctorId]);

  const onClearDoctorFilter = () => {
    setSelectedDoctorId("");
    // This will trigger the useEffect to reload all
  };

  return (
    <div className="shift-wrap">
      {/* Filter Bar */}
      <div className="shift-filterbar">
        <div className="left">
          <h5 className="title">Daily Shifts</h5>
        </div>

        <div className="right">
          <Form.Control
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="date-input"
          />

          <Form.Select
            value={selectedDoctorId}
            onChange={(e) => setSelectedDoctorId(e.target.value)}
          >
            <option value="">-- All doctors --</option>
            {doctors.map((d) => (
              <option key={d.id} value={String(d.id)}>
                {d.fullName} {d.specialtyName ? `- ${d.specialtyName}` : ""}
              </option>
            ))}
          </Form.Select>

          <Button variant="outline-secondary" onClick={onClearDoctorFilter} disabled={loading}>
            Reset
          </Button>
        </div>
      </div>

      {/* Error / Loading */}
      {error && (
        <Card body className="border-danger-subtle text-danger mb-3">
          {error}
        </Card>
      )}
      {loading ? (
        <div className="loading-inline">
          <Spinner animation="border" size="sm" />
          <span>Đang tải…</span>
        </div>
      ) : (
        <div className="shift-table-responsive">
          <table className="shift-table table table-bordered align-middle">
            <thead>
              <tr>
                <th className="col-doctor">Doctor</th>
                <th className="col-spec">Specialty</th>
                <th className="col-shift">Shift</th>
                <th className="col-day">Appointments</th>
              </tr>
            </thead>
            <tbody>
              {shifts.length === 0 && (
                <tr>
                  <td colSpan={4} className="text-center">
                    {loading ? "Loading..." : "No data"}
                  </td>
                </tr>
              )}
              {shifts.map((shift) => {
                const appointments = shiftAppointments[shift.id] || [];
                return (
                  <tr key={shift.id}>
                    <td className="fw-semibold">{shift.doctorName}</td>
                    <td>{shift.specialtyName || "—"}</td>
                    <td>{shift.shift}</td>
                    <td className="cell">
                      {appointments.length > 0 ? (
                        <div className="cell-inner">
                          <div className="appt-list">
                            {appointments
                              .sort((a, b) => {
                                const timeA = a.appointmentTime || "";
                                const timeB = b.appointmentTime || "";
                                return timeA.localeCompare(timeB);
                              })
                              .map((appt) => (
                                <AppointmentChip
                                  key={appt.id}
                                  time={appt.appointmentTime}
                                  patient={appt.patientName}
                                  status={appt.status}
                                />
                              ))}
                          </div>
                        </div>
                      ) : (
                        <span className="cell-empty">—</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

/** ====== Appointment Chip Component ====== */
function AppointmentChip({
  time,
  patient,
  status,
}: {
  time?: string;
  patient?: string;
  status?: string;
}) {
  const s = String(status || "").toLowerCase().replace(/_/g, "-");
  const cls = s ? `appt appt-${s}` : "appt appt-default";
  return (
    <div className={cls} title={status || ""}>
      <span className="appt-left">
        <span className="time">{(time || "--:--").slice(0, 5)}</span>
        <span className="patient">{patient || "Patient"}</span>
      </span>
      <span className="status">{status || "-"}</span>
    </div>
  );
}
