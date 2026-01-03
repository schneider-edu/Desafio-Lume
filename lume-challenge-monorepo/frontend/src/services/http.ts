import axios from "axios";
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from "../auth/tokenStorage";

const baseURL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export const http = axios.create({ baseURL });

let refreshing: Promise<void> | null = null;

http.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (r) => r,
  async (error) => {
    const original: any = error.config;
    if (error.response?.status !== 401 || original?._retry) {
      throw error;
    }

    original._retry = true;

    if (!refreshing) {
      refreshing = (async () => {
        const refreshToken = getRefreshToken();
        if (!refreshToken) {
          clearTokens();
          return;
        }
        try {
          const res = await axios.post(`${baseURL}/auth/refresh`, { refreshToken });
          setTokens(res.data.accessToken, res.data.refreshToken);
        } catch {
          clearTokens();
        } finally {
          refreshing = null;
        }
      })();
    }

    await refreshing;
    const newAccess = getAccessToken();
    if (!newAccess) throw error;

    original.headers = original.headers ?? {};
    original.headers.Authorization = `Bearer ${newAccess}`;
    return http.request(original);
  }
);
