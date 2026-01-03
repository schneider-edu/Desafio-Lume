import { Navigate, Route, Routes } from "react-router-dom";
import { Container } from "@mui/material";
import LoginPage from "./pages/LoginPage";
import ClientsPage from "./pages/ClientsPage";
import PrivateRoute from "./routes/PrivateRoute";

export default function App() {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/clients"
          element={
            <PrivateRoute>
              <ClientsPage />
            </PrivateRoute>
          }
        />
        <Route path="*" element={<Navigate to="/clients" replace />} />
      </Routes>
    </Container>
  );
}
