import { http } from "./http";

export type Specialty = {
  id: number;
  name: string;
  description: string;
};

export type SpecialtyCreatePayload = {
  name: string;
  description: string;
};

export type SpecialtyUpdatePayload = {
  name: string;
  description: string;
};

function pickArray(data: any): Specialty[] {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.data)) return data.data;   // <-- BE của bạn dùng 'data'
  return [];
}


export async function fetchSpecialties(): Promise<Specialty[]> {
  const res = await http.get("/specialties/all");
  return pickArray(res.data);
}


export async function createSpecialty(payload: SpecialtyCreatePayload): Promise<Specialty> {
  const res = await http.post("/specialties/create", payload);
  const arr = pickArray(res.data);
  if (arr.length) return arr[0] as Specialty;
  return (res.data?.data ?? res.data) as Specialty;
}


export async function updateSpecialty(id: number, payload: SpecialtyUpdatePayload): Promise<Specialty> {
  const res = await http.put(`/specialties/update/${id}`, payload);
  const arr = pickArray(res.data);
  if (arr.length) return arr[0] as Specialty;
  return (res.data?.data ?? res.data) as Specialty;
}

/**
 * Fetch specialty by ID from /all endpoint and filter
 * (Backend doesn't have /{id} endpoint, so we fetch all and filter)
 */
export async function fetchSpecialtyById(id: number): Promise<Specialty | null> {
  try {
    const specialties = await fetchSpecialties();
    const specialty = specialties.find(s => s.id === id);
    return specialty || null;
  } catch (error) {
    console.error('Error fetching specialty:', error);
    return null;
  }
}