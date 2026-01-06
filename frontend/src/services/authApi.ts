import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export type TokenResponse = { accessToken: string; refreshToken: string };

export const authApi = {
  async register(email: string, password: string) {
    await axios.post(`${baseURL}/auth/register`, { email, password });
  },
  async login(email: string, password: string): Promise<TokenResponse> {
    const res = await axios.post(`${baseURL}/auth/login`, { email, password });
    return res.data;
  },
  async logout(refreshToken: string) {
    await axios.post(`${baseURL}/auth/logout`, { refreshToken });
  }
};
