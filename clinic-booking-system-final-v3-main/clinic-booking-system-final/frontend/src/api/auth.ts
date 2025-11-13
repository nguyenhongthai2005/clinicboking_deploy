import { http } from './http';

export type LoginPayload = { email: string; password: string };
export type LoginResponse = {
  token: string;
  user: { id: string; fullName: string; email: string; userType: string };
};

export type RegistrationInput = {
  fullName: string;
  email: string;
  phoneNumber: string;
  password: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
};
export type RegisterResponse = { id: string; email: string };

// Backend API Response wrapper
type ApiResponse<T> = {
  message: string;
  data: T;
};

// Backend LoginResponse structure
type BackendLoginResponse = {
  message: string;
  success: boolean;
  id: number;
  fullName: string;
  role: string;
  token: string;
};

export async function loginApi(payload: LoginPayload): Promise<LoginResponse> {
  const { data } = await http.post<ApiResponse<BackendLoginResponse>>(
    '/api/v1/auth/login',      // ✅ thêm /api/v1
    payload
  );

  const backend = data.data;
  if (!backend || !backend.token) {
    throw new Error(data?.message || 'Đăng nhập thất bại');
  }

  return {
    token: backend.token,
    user: {
      id: String(backend.id),
      fullName: backend.fullName,
      email: payload.email,
      userType: backend.role,
    },
  };
}

export async function registerApi(
  payload: RegistrationInput
): Promise<RegisterResponse> {
  const { data } = await http.post<ApiResponse<{ id: number; email: string }>>(
    '/api/v1/auth/register',   // ✅ thêm /api/v1
    payload
  );

  const backendResponse = data.data;

  return {
    id: backendResponse.id.toString(),
    email: backendResponse.email,
  };
}

export function registerAdminApi(payload: RegistrationInput) {
  return http.post('/api/v1/admin/register-admin', payload); // ✅ thêm /api/v1
}
