// src/api/receptionist.ts
import { http } from "./http";

export type Receptionist = {
  id: number;
  fullName: string;
  email?: string;
  phoneNumber?: string;
  gender?: "Male" | "Female" | "Other";
};

export type CreateReceptionistPayload = {
  fullName: string;
  email: string;
  phoneNumber: string;
  password: string;
  gender: "Male" | "Female" | "Other";
};

export type UpdateReceptionistPayload = {
  fullName?: string;
  phoneNumber?: string;
  gender?: "Male" | "Female" | "Other";
};

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

function normalizeReceptionist(raw: any): Receptionist {
  if (!raw || typeof raw !== "object") return raw as Receptionist;
  return {
    id: Number(raw.id),
    fullName: raw.fullName ?? raw.name ?? "",
    email: raw.email,
    phoneNumber: raw.phoneNumber,
    gender: raw.gender,
  };
}
function normalizeReceptionistArray(list: any[]): Receptionist[] {
  return (list || []).map(normalizeReceptionist);
}

// GET /api/v1/receptionists/all
export async function fetchReceptionistsAll(): Promise<Receptionist[]> {
  const res = await http.get("/receptionists/all");
  return normalizeReceptionistArray(pickArray(res.data));
}

// GET /api/v1/receptionists/{id}
export async function fetchReceptionistById(id: number): Promise<Receptionist> {
  const res = await http.get(`/receptionists/${id}`);
  return normalizeReceptionist(pickObject(res.data));
}

// POST /api/v1/admin/create-user with userType Receptionist
export async function createReceptionist(payload: CreateReceptionistPayload): Promise<Receptionist> {
  const res = await http.post("/admin/create-user", {
    ...payload,
    userType: "Receptionist",
  });
  return pickObject<Receptionist>(res.data);
}

// PUT /api/v1/receptionists/update/{id}
export async function updateReceptionist(id: number, payload: UpdateReceptionistPayload): Promise<Receptionist> {
  const res = await http.put(`/receptionists/update/${id}`, payload);
  return normalizeReceptionist(pickObject(res.data));
}


