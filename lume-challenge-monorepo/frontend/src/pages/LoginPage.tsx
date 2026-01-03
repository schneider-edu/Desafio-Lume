import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Alert, Box, Button, Card, CardContent, Stack, TextField, Typography } from "@mui/material";
import { useAuth } from "../auth/AuthContext";

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const nav = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const disabled = useMemo(() => !email || !password, [email, password]);

  useEffect(() => {
    if (isAuthenticated) nav("/clients", { replace: true });
  }, [isAuthenticated]);

  const onSubmit = async () => {
    setError(null);
    try {
      await login(email, password);
      nav("/clients", { replace: true });
    } catch {
      setError("Falha no login. Verifique e-mail e senha.");
    }
  };

  return (
    <Box sx={{ display: "grid", placeItems: "center", minHeight: "70vh" }}>
      <Card sx={{ width: "100%", maxWidth: 520 }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="h5">Login</Typography>

            {error && <Alert severity="error">{error}</Alert>}

            <TextField
              label="E-mail"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              fullWidth
            />
            <TextField
              label="Senha"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              fullWidth
            />

            <Button variant="contained" onClick={onSubmit} disabled={disabled}>
              Entrar
            </Button>

            <Typography variant="body2" color="text.secondary">
              Primeiro, registre um usuário via Swagger: POST /auth/register
            </Typography>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
