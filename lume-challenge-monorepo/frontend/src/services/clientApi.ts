import { http } from "./http";

export type Client = {
  id: number;
  name: string;
  cpf: string;
  cep: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  uf: string;
  numero: string;
  complemento: string;
};

export type ClientRequest = Omit<Client, "id">;

export const clientApi = {
  async list(params?: { cep?: string; name?: string }): Promise<Client[]> {
    const res = await http.get("/clients", { params });
    return res.data;
  },
  async get(id: number): Promise<Client> {
    const res = await http.get(`/clients/${id}`);
    return res.data;
  },
  async create(payload: ClientRequest): Promise<Client> {
    const res = await http.post("/clients", payload);
    return res.data;
  },
  async update(id: number, payload: ClientRequest): Promise<Client> {
    const res = await http.put(`/clients/${id}`, payload);
    return res.data;
  },
  async remove(id: number): Promise<void> {
    await http.delete(`/clients/${id}`);
  }
};
