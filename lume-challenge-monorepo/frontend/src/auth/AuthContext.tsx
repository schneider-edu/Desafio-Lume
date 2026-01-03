import React, { createContext, useContext, useMemo, useState } from "react";
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from "./tokenStorage";
import { authApi } from "../services/authApi";

type AuthContextValue = {
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  tokens: { accessToken: string | null; refreshToken: string | null };
  setTokenPair: (accessToken: string, refreshToken: string) => void;
  clear: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(getAccessToken());
  const [refreshToken, setRefreshToken] = useState<string | null>(getRefreshToken());

  const setTokenPair = (access: string, refresh: string) => {
    setTokens(access, refresh);
    setAccessToken(access);
    setRefreshToken(refresh);
  };

  const clear = () => {
    clearTokens();
    setAccessToken(null);
    setRefreshToken(null);
  };

  const login = async (email: string, password: string) => {
    const res = await authApi.login(email, password);
    setTokenPair(res.accessToken, res.refreshToken);
  };

  const logout = async () => {
    if (refreshToken) {
      try {
        await authApi.logout(refreshToken);
      } finally {
        clear();
      }
    } else {
      clear();
    }
  };

  const value = useMemo<AuthContextValue>(
    () => ({
      isAuthenticated: Boolean(accessToken && refreshToken),
      login,
      logout,
      tokens: { accessToken, refreshToken },
      setTokenPair,
      clear
    }),
    [accessToken, refreshToken]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
