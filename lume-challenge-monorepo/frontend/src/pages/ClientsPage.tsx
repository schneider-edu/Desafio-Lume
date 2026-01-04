import { useEffect, useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  IconButton,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import LogoutIcon from "@mui/icons-material/Logout";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { useAuth } from "../auth/AuthContext";
import { Client, ClientRequest, clientApi } from "../services/clientApi";
import ClientFormDialog from "../components/ClientFormDialog";

export default function ClientsPage() {
  const { logout } = useAuth();

  const [cepQuery, setCepQuery] = useState("");
  const [nameQuery, setNameQuery] = useState("");
  const [rows, setRows] = useState<Client[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Client | null>(null);

  const load = async () => {
    setError(null);
    setLoading(true);
    try {
      const data = await clientApi.list({
        cep: cepQuery || undefined,
        name: nameQuery || undefined
      });
      setRows(data);
    } catch {
      setError("Falha ao carregar clientes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const columns = useMemo<GridColDef[]>(
    () => [
      { field: "id", headerName: "ID", width: 80 },
      { field: "name", headerName: "Nome", flex: 1, minWidth: 200 },
      { field: "cpf", headerName: "CPF", width: 140 },
      { field: "cep", headerName: "CEP", width: 110 },
      { field: "cidade", headerName: "Cidade", width: 160 },
      { field: "uf", headerName: "UF", width: 80 },
      {
        field: "actions",
        headerName: "Ações",
        width: 140,
        sortable: false,
        filterable: false,
        renderCell: (params) => (
          <Stack direction="row" spacing={1}>
            <IconButton
              size="small"
              onClick={() => {
                setEditing(params.row as Client);
                setOpen(true);
              }}
            >
              <EditIcon fontSize="small" />
            </IconButton>
            <IconButton
              size="small"
              onClick={async () => {
                const ok = confirm("Excluir este cliente?");
                if (!ok) return;
                try {
                  await clientApi.remove((params.row as Client).id);
                  await load();
                } catch {
                  setError("Falha ao excluir.");
                }
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Stack>
        )
      }
    ],
    [rows]
  );

  const onSave = async (payload: ClientRequest, id?: number) => {
    if (id) {
      await clientApi.update(id, payload);
    } else {
      await clientApi.create(payload);
    }
    await load();
  };

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h4">Clientes</Typography>
        <Stack direction="row" spacing={1}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => {
              setEditing(null);
              setOpen(true);
            }}
          >
            Novo
          </Button>
          <IconButton onClick={logout} title="Sair">
            <LogoutIcon />
          </IconButton>
        </Stack>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Stack direction={{ xs: "column", md: "row" }} spacing={2} sx={{ mb: 2 }}>
        <TextField
          label="Buscar por CEP"
          value={cepQuery}
          onChange={(e) => setCepQuery(e.target.value.replace(/\D/g, ""))}
          fullWidth
        />
        <TextField
          label="Buscar por nome"
          value={nameQuery}
          onChange={(e) => setNameQuery(e.target.value)}
          fullWidth
        />
        <Button variant="outlined" onClick={load} disabled={loading}>
          Buscar
        </Button>
      </Stack>

      <div style={{ height: 520, width: "100%" }}>
        <DataGrid rows={rows} columns={columns} loading={loading} disableRowSelectionOnClick />
      </div>

      <ClientFormDialog
        open={open}
        initial={editing}
        onClose={() => setOpen(false)}
        onSave={onSave}
      />
    </Box>
  );
}
