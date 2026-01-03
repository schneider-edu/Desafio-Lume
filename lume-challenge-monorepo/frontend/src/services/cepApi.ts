import { http } from "./http";

export type CepResponse = {
  cep: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  uf: string;
};

export const cepApi = {
  async get(cep: string): Promise<CepResponse> {
    const res = await http.get(`/cep/${cep}`);
    return res.data;
  }
};
