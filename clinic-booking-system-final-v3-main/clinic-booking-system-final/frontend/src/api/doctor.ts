// src/api/doctor.ts
import { http } from "./http";

/** ---- Domain types (theo BE hiện có) ---- */
export type Doctor = {
  id: number;
  fullName: string;

  email?: string;
  phoneNumber?: string;
  gender?: "Male" | "Female" | "Other";

  degree?: string;
  description?: string;
  experience?: string;

  specialtyId?: number;
  specialtyName?: string;
};

export type CreateDoctorPayload = {
  fullName: string;
  email: string;
  phoneNumber: string;
  password: string;
  gender: "Male" | "Female" | "Other";
  specialization: string;
  degree?: string;
  description?: string;
  experience?: string;
};

export type UpdateDoctorPayload = {
  fullName?: string;
  phoneNumber?: string;
  gender?: "Male" | "Female" | "Other";
  specialtyId?: number;
  degree?: string;
  description?: string;
  experience?: string;
};

/* Chuẩn hoá response */
function pickArray<T = any>(data: any): T[] {
  if (Array.isArray(data)) return data as T[];
  if (Array.isArray(data?.items)) return data.items as T[];
  if (Array.isArray(data?.data)) return data.data as T[];
  return [];
}
function pickObject<T = any>(data: any): T {
  if (data?.data && typeof data.data === "object") return data.data as T;
  return data as T;
}

/* Map về shape FE thống nhất */
function normalizeDoctor(raw: any): Doctor {
  if (!raw || typeof raw !== "object") return raw as Doctor;

  const specialtyId =
    raw.specialtyId ?? raw.specialty?.id ?? raw.specialityId ?? raw.specialities?.[0]?.id;
  const specialtyName =
    raw.specialtyName
      ?? raw.specialty?.name
      ?? raw.specialityName
      ?? raw.specialities?.[0]?.name
      ?? raw.specialization // BE AdminController/DoctorService uses 'specialization'
    ;

  return {
    id: Number(raw.id),
    fullName: raw.fullName ?? raw.name ?? "",
    email: raw.email,
    phoneNumber: raw.phoneNumber,
    gender: raw.gender,
    degree: raw.degree,
    description: raw.description,
    experience: raw.experience,
    specialtyId,
    specialtyName,
  };
}
function normalizeDoctorArray(list: any[]): Doctor[] {
  return (list || []).map(normalizeDoctor);
}

/** ================== API CALLS ================== */
/** GET /api/v1/doctors/all */
export async function fetchDoctorsAll(): Promise<Doctor[]> {
  const res = await http.get("/admin/doctors");
  return normalizeDoctorArray(pickArray(res.data));
}

/** GET /api/v1/doctors/by-specialty/{specialtyId} */
export async function fetchDoctorsBySpecialty(specialtyId: number): Promise<Doctor[]> {
  const res = await http.get(`/doctors/by-specialty/${specialtyId}`);
  return normalizeDoctorArray(pickArray(res.data));
}

/** GET /api/v1/doctors/{id} */
export async function fetchDoctorById(id: number): Promise<Doctor> {
  const res = await http.get(`/doctors/${id}`);
  return normalizeDoctor(pickObject(res.data));
}

/** GET /api/v1/doctors/search?keyword=... */
export async function searchDoctors(keyword: string): Promise<Doctor[]> {
  const res = await http.get(`/doctors/search?keyword=${encodeURIComponent(keyword)}`);
  return normalizeDoctorArray(pickArray(res.data));
}

/**
 * POST /api/v1/admin/create-user
 * - BE tạo user bác sĩ; nếu BE dùng endpoint khác, đổi URL dưới.
 * - Nếu BE tự set role/authority cho bác sĩ thì bỏ `role`.
 */
export async function createDoctor(payload: CreateDoctorPayload): Promise<Doctor> {
  const res = await http.post("/admin/create-user", {
    ...payload,
    userType: "Doctor", // đúng key và đúng casing như Postman của bạn
  });
  return pickObject<Doctor>(res.data);
}

/** PUT /api/v1/doctors/update/{id} */
export async function updateDoctor(id: number, payload: UpdateDoctorPayload): Promise<Doctor> {
  const res = await http.put(`/admin/update-doctor/${id}`, payload);
  return normalizeDoctor(pickObject(res.data));
}
