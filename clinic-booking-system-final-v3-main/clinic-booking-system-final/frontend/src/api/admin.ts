import { http } from './http';

export type AdminUser = {
  id: number;
  fullName: string;
  email?: string;
  phoneNumber?: string;
  gender?: string;
  degree?: string;
  description?: string;
  experience?: string;
};

function pickArray<T = any>(data: any): T[] {
  if (Array.isArray(data)) return data as T[];
  if (Array.isArray(data?.items)) return data.items as T[];
  if (Array.isArray(data?.data)) return data.data as T[];
  return [];
}

function normalizeUser(raw: any): AdminUser {
  if (!raw || typeof raw !== 'object') return raw as AdminUser;
  return {
    id: Number(raw.id),
    fullName: raw.fullName ?? raw.name ?? '',
    email: raw.email,
    phoneNumber: raw.phoneNumber,
    gender: raw.gender,
    degree: raw.degree,
    description: raw.description,
    experience: raw.experience,
  };
}

function normalizeUserArray(list: any[]): AdminUser[] {
  return (list || []).map(normalizeUser);
}

export async function fetchAdminUsers(): Promise<AdminUser[]> {
  const res = await http.get('/admin/users');
  return normalizeUserArray(pickArray(res.data));
}

export async function fetchAdminReceptionists(): Promise<AdminUser[]> {
  const res = await http.get('/admin/receptionists');
  return normalizeUserArray(pickArray(res.data));
}

