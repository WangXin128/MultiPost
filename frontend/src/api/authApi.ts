import { apiClient, setToken, unwrap } from './client';
import type { AuthResponse, CurrentUser } from '../types';

export async function register(payload: { username: string; email: string; password: string }) {
  const data = await unwrap<AuthResponse>(apiClient.post('/api/auth/register', payload));
  setToken(data.token);
  return data;
}

export async function login(payload: { email: string; password: string }) {
  const data = await unwrap<AuthResponse>(apiClient.post('/api/auth/login', payload));
  setToken(data.token);
  return data;
}

export function me() {
  return unwrap<CurrentUser>(apiClient.get('/api/auth/me'));
}
