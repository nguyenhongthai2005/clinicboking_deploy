// src/api/prescription.ts
import { http } from "./http";

/** ===== Types ===== */
export type Prescription = {
  id: number;
  medicineName?: string | null;
  dosage?: string | null;
  duration?: string | null;
  instructions?: string | null;
  createdAt?: string;
  appointmentId?: number;
};

export type CreatePrescriptionPayload = {
  medicineName?: string | null;
  dosage?: string | null;
  duration?: string | null;
  instructions?: string | null;
};

export type ActionResponse<T = any> = {
  ok: boolean;
  data?: T;
  error?: string;
};

/** ===== Utilities ===== */
const normalizePrescription = (raw: any): Prescription => ({
  id: Number(raw.id),
  medicineName: raw.medicineName,
  dosage: raw.dosage,
  duration: raw.duration,
  instructions: raw.instructions,
  createdAt: raw.createdAt,
  appointmentId: raw.appointmentId || raw.appointment?.id,
});

/** 🔵 DOCTOR — Tạo prescription cho appointment */
export async function createPrescription(
  appointmentId: number,
  payload?: CreatePrescriptionPayload
): Promise<ActionResponse<Prescription>> {
  try {
    const body = payload || {
      medicineName: null,
      dosage: null,
      duration: null,
      instructions: null,
    };
    const res = await http.post(`/prescriptions/create/${appointmentId}`, body);
    const prescription = normalizePrescription(res.data?.data ?? res.data);
    return { ok: true, data: prescription };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to create prescription" };
  }
}

/** Lấy danh sách prescriptions theo appointment ID */
export async function getPrescriptionsByAppointment(
  appointmentId: number
): Promise<ActionResponse<Prescription[]>> {
  try {
    const res = await http.get(`/prescriptions/by-appointment/${appointmentId}`);
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizePrescription) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to fetch prescriptions" };
  }
}

/** 🔵 DOCTOR — Tạo nhiều prescriptions cho appointment cùng lúc */
export async function createPrescriptions(
  appointmentId: number,
  prescriptions: CreatePrescriptionPayload[]
): Promise<ActionResponse<Prescription[]>> {
  try {
    const res = await http.post(`/prescriptions/batch-create/${appointmentId}`, prescriptions);
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizePrescription) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to create prescriptions" };
  }
}

/** 🔵 DOCTOR — Cập nhật prescriptions cho appointment (chỉ khi chưa completed) */
export async function updatePrescriptions(
  appointmentId: number,
  prescriptions: CreatePrescriptionPayload[]
): Promise<ActionResponse<Prescription[]>> {
  try {
    const res = await http.put(`/prescriptions/update/${appointmentId}`, prescriptions);
    const payload = Array.isArray(res.data?.data)
      ? res.data.data
      : Array.isArray(res.data)
      ? res.data
      : [];
    return { ok: true, data: (payload || []).map(normalizePrescription) };
  } catch (e: any) {
    return { ok: false, error: e?.message || "Failed to update prescriptions" };
  }
}

