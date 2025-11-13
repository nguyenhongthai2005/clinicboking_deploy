import { http } from "./http";

export type Service = {
  id: number;
  name: string;
  description: string;
};

function pickArray(data: any): Service[] {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.data)) return data.data;
  return [];
}

export async function fetchServices(): Promise<Service[]> {
  const res = await http.get("/services/all");
  return pickArray(res.data);
}

export async function fetchService(id: number | string): Promise<Service | null> {
  const res = await http.get(`/services/${id}`);
  const arr = pickArray(res.data);
  if (arr.length) return arr[0] as Service;
  return (res.data?.data ?? res.data ?? null) as Service | null;
}


