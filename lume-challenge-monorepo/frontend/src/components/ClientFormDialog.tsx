import { useEffect, useMemo, useState } from "react";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Stack,
  TextField
} from "@mui/material";
import { Client, ClientRequest } from "../services/clientApi";
import { cepApi } from "../services/cepApi";

type Props = {
  open: boolean;
  initial?: Client | null;
  onClose: () => void;
  onSave: (payload: ClientRequest, id?: number) => Promise<void>;
};

const empty: ClientRequest = {
  name: "",
  cpf: "",
  cep: "",
  logradouro: "",
  bairro: "",
  cidade: "",
  uf: "",
  numero: "",
  complemento: ""
};

export default function ClientFormDialog({ open, initial, onClose, onSave }: Props) {
  const [form, setForm] = useState<ClientRequest>(empty);
  const [error, setError] = useState<string | null>(null);
  const [loadingCep, setLoadingCep] = useState(false);

  const isEdit = Boolean(initial?.id);

  useEffect(() => {
  if (!open) return;
  setError(null);
  if (!initial) {
    setForm(empty);
    return;
  }
  setForm({
    name: initial.name,
    cpf: initial.cpf,
    cep: initial.cep,
    logradouro: initial.logradouro,
    bairro: initial.bairro,
    cidade: initial.cidade,
    uf: initial.uf,
    numero: initial.numero,
    complemento: initial.complemento ?? ""
  });
}, [open, initial]);

  const canSave = useMemo(() => {
    return form.name && form.cpf && form.cep && form.numero;
  }, [form]);

  const setField = (k: keyof ClientRequest, v: string) => {
    setForm((p) => ({ ...p, [k]: v }));
  };

  const onLookupCep = async () => {
    setError(null);
    const cep = form.cep.replace(/\D/g, "");
    if (cep.length !== 8) {
      setError("CEP deve ter 8 dígitos.");
      return;
    }
    setLoadingCep(true);
    try {
      const res = await cepApi.get(cep);
      setForm((p) => ({
        ...p,
        cep: res.cep,
        logradouro: p.logradouro || res.logradouro,
        bairro: p.bairro || res.bairro,
        cidade: p.cidade || res.cidade,
        uf: p.uf || res.uf
      }));
    } catch {
      setError("Falha ao buscar CEP.");
    } finally {
      setLoadingCep(false);
    }
  };

  const submit = async () => {
    setError(null);
    try {
      await onSave(form, initial?.id);
      onClose();
    } catch (e: any) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data?.fieldErrors?.[0]?.message ||
        "Falha ao salvar cliente.";
      setError(msg);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>{isEdit ? "Editar cliente" : "Novo cliente"}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          {error && <Alert severity="error">{error}</Alert>}

          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <TextField
                label="Nome"
                value={form.name}
                onChange={(e) => setField("name", e.target.value)}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                label="CPF"
                value={form.cpf}
                onChange={(e) => setField("cpf", e.target.value)}
                fullWidth
              />
            </Grid>

            <Grid item xs={12} md={4}>
              <TextField
                label="CEP"
                value={form.cep}
                onChange={(e) => setField("cep", e.target.value)}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <Button variant="outlined" onClick={onLookupCep} disabled={loadingCep} sx={{ height: "56px" }}>
                Buscar CEP
              </Button>
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                label="Número"
                value={form.numero}
                onChange={(e) => setField("numero", e.target.value)}
                fullWidth
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Logradouro"
                value={form.logradouro}
                onChange={(e) => setField("logradouro", e.target.value)}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                label="Bairro"
                value={form.bairro}
                onChange={(e) => setField("bairro", e.target.value)}
                fullWidth
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Cidade"
                value={form.cidade}
                onChange={(e) => setField("cidade", e.target.value)}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <TextField
                label="UF"
                value={form.uf}
                onChange={(e) => setField("uf", e.target.value.toUpperCase())}
                inputProps={{ maxLength: 2 }}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                label="Complemento"
                value={form.complemento}
                onChange={(e) => setField("complemento", e.target.value)}
                fullWidth
              />
            </Grid>
          </Grid>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={submit} disabled={!canSave}>
          Salvar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
