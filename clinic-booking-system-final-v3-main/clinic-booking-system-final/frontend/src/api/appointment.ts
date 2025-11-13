// src/services/appointment.ts
import { http } from "../api/http";
import type { UiStatus } from "../components/appointment/AppointmentStatus";
import { normStatus, toBackendStatus } from "../components/appointment/AppointmentStatus";

/** ===== Types ===== */
export type Appointment = {
  id: number;
  appointmentDate?: string;  // yyyy-mm-dd
  appointmentTime?: string;  // HH:mm
  appointmentNo?: string;
  createdAt?: string;
  status: UiStatus;
  patientName?: string;
  doctorName?: string;
  doctorId?: number;  // For filtering shifts by doctor during reschedule
  specialtyName?: string;
  reason?: string;
};

export type AppointmentQuery = {
  status: UiStatus;
  date?: string;             // default: today (yyyy-mm-dd)
  specialty?: string;        // optional filter client-side
  search?: string;           // by patient/doctor, client-side
};

export type ActionResponse<T = any> = {
  ok: boolean;
  data?: T;
  error?: string;
};

/** ===== Utilities ===== */
export const toISODateInput = (d = new Date()): string => {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
};

export const normalizeAppointment = (raw: any): Appointment => ({
  id: Number(raw.id),
  appointmentDate: raw.appointmentDate ?? raw.date,
  appointmentTime: (raw.appointmentTime ?? raw.time ?? "").slice(0, 5),
  appointmentNo: raw.appointmentNo,
  createdAt: raw.createdAt,
  status: normStatus(raw.status),
  patientName: raw.patientName ?? raw.patient?.fullName ?? raw.patient,
  doctorName: raw.doctorName ?? raw.doctor?.fullName ?? raw.doctor,
  doctorId: raw.doctorId ?? raw.doctor?.id ?? raw.shift?.doctorId ?? raw.shift?.doctor?.id,
  specialtyName: raw.specialtyName ?? raw.specialty?.name,
  reason: raw.reason,
});

/** Client-side paging helper (cho list đã tải) */
export const paginate = <T,>(items: T[], page: number, pageSize: number) => {
  const start = (page - 1) * pageSize;
  return items.slice(start, start + pageSize);
};

/** Lấy danh sách theo status + date (server) */
export async function fetchByStatusAndDate(
  status: UiStatus,
  date: string = toISODateInput()
): Promise<ActionResponse<Appointment[]>> {
  try {
    // Convert frontend status format to backend format
    const backendStatus = toBackendStatus(status);
    const res = await http.get(
      `/appointments/status/${encodeURIComponent(backendStatus)}?date=${encodeURIComponent(date)}`
    );
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizeAppointment) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch appointments" };
  }
}

/** Lấy tất cả appointments theo date (server) */
export async function fetchAllByDate(
  date: string = toISODateInput()
): Promise<ActionResponse<Appointment[]>> {
  try {
    const res = await http.get(
      `/appointments/by-date?date=${encodeURIComponent(date)}`
    );
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizeAppointment) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch all appointments" };
  }
}

/** Lấy chi tiết theo id */
export async function fetchDetail(id: number): Promise<ActionResponse<Appointment>> {
  try {
    const res = await http.get(`/appointments/${id}`);
    return { ok: true, data: normalizeAppointment(res.data?.data ?? res.data) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch detail" };
  }
}

/** ===== Actions (giữ nguyên endpoint bạn đã có) ===== */
export async function confirmAppointment(id: number): Promise<ActionResponse> {
  try {
    await http.put(`/appointments/confirm/${id}`);
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to confirm appointment" };
  }
}

export async function checkinAppointment(id: number): Promise<ActionResponse> {
  try {
    await http.put(`/appointments/checkin/${id}`);
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to check-in" };
  }
}

export async function cancelAppointment(id: number): Promise<ActionResponse> {
  try {
    await http.put(`/appointments/cancel/${id}`);
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to cancel" };
  }
}

/** 🟡 RECEPTIONIST/ADMIN — Reschedule appointment với shift mới */
export async function rescheduleAppointment(
  id: number,
  newShiftId: number
): Promise<ActionResponse> {
  try {
    await http.put(`/appointments/reschedule/${id}?newShiftId=${encodeURIComponent(newShiftId)}`);
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to reschedule" };
  }
}

/** 🔵 DOCTOR — Lấy appointments theo shift ID */
export async function fetchByShift(shiftId: number): Promise<ActionResponse<Appointment[]>> {
  try {
    const res = await http.get(`/appointments/by-shift/${shiftId}`);
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizeAppointment) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch appointments by shift" };
  }
}

/** 🔵 DOCTOR — Lấy appointments theo nhiều shift IDs */
export async function fetchByShifts(shiftIds: number[]): Promise<ActionResponse<Appointment[]>> {
  try {
    const results = await Promise.all(shiftIds.map((id) => fetchByShift(id)));
    const all: Appointment[] = [];
    for (const res of results) {
      if (res.ok && res.data) {
        all.push(...res.data);
      }
    }
    return { ok: true, data: all };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch appointments by shifts" };
  }
}

/** 🔵 DOCTOR — Bắt đầu khám (CHECKED_IN → IN_PROGRESS) */
export async function startAppointment(id: number): Promise<ActionResponse<Appointment>> {
  try {
    await http.put(`/appointments/start/${id}`);
    // Reload appointment để lấy status mới nhất
    const detailRes = await fetchDetail(id);
    if (detailRes.ok && detailRes.data) {
      return { ok: true, data: detailRes.data };
    }
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to start appointment" };
  }
}

/** 🔵 DOCTOR — Hoàn tất khám (IN_PROGRESS → COMPLETED) */
export async function completeAppointment(id: number): Promise<ActionResponse> {
  try {
    await http.put(`/appointments/complete/${id}`);
    return { ok: true };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to complete appointment" };
  }
}

/** ===== Phụ trợ lọc client-side (chung cho mọi trang) ===== */
export function deriveSpecialties(list: Appointment[]): string[] {
  return [...new Set(list.map((x) => x.specialtyName).filter(Boolean) as string[])];
}

export function applyClientFilters(
  list: Appointment[],
  opts: { specialty?: string; search?: string }
): Appointment[] {
  let out = [...list];
  if (opts.specialty && opts.specialty !== "ALL") {
    out = out.filter((x) => x.specialtyName === opts.specialty);
  }
  if (opts.search?.trim()) {
    const q = opts.search.trim().toLowerCase();
    out = out.filter(
      (x) =>
        (x.patientName || "").toLowerCase().includes(q) ||
        (x.doctorName || "").toLowerCase().includes(q)
    );
  }
  return out;
}
