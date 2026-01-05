# Desafio Tecnico - Spring Boot (Java 21) + React

Monorepo com:
- `backend/`: API Spring Boot (Java 21, Maven, H2, JPA, Security com JWT + Refresh Token, Swagger)
- `frontend/`: React (Vite + TS + MUI) com login e CRUD de clientes (busca por nome/cep)

---

## Rodando com Docker + seed (fluxo rapido)
Requisitos: Docker + Docker Compose. Para rodar o seed, tenha `bash` + `curl` (Git Bash/WSL no Windows). `jq` ou `node` ajudam a ler o JSON, mas nao sao obrigatorios.

1) Suba os servicos:
```bash
docker compose up --build
```
- Backend: `http://localhost:8080` (Swagger em `/swagger-ui`)
- Frontend: `http://localhost:5173`

2) Popular a base com usuarios e clientes de exemplo:
```bash
cd scripts
API_URL=http://localhost:8080 bash seed.sh
```
- Usuarios criados (ou reutilizados se ja existirem):
  - `admin@lume.dev` / `senha123`
  - `user@lume.dev` / `senha123`
- Cria 10 clientes com CEP `01001000` e mostra accessToken + refreshToken do admin.
- Variaveis opcionais: `API_URL`, `USER_EMAIL_1`/`USER_PASS_1`, `USER_EMAIL_2`/`USER_PASS_2`, `CEP_DEFAULT`.
- O H2 e em memoria; se os containers reiniciarem, rode o seed de novo.

3) Testar o fluxo com os dados seed:
- Swagger: abra `http://localhost:8080/swagger-ui`, faca `POST /auth/login` ou use o accessToken impresso pelo seed, clique em **Authorize** (Bearer) e chame `/clients` ou `/cep/{cep}`.
- Frontend: acesse `http://localhost:5173`, login com `admin@lume.dev` / `senha123`. A lista ja vem com os 10 clientes; busque por nome/CEP, crie/edite/exclua ou use **Buscar CEP** no formulario para preencher endereco.

---

## Como rodar local (sem Docker)

### Backend
```bash
cd backend
mvn spring-boot:run
```
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:challenge`
  - user: `sa`
  - password: (vazio)

### Frontend
```bash
cd frontend
npm install
npm run dev
```
- Frontend: `http://localhost:5173`
- Por padrao, o frontend aponta para `http://localhost:8080`. Para mudar:
```bash
# frontend/.env
VITE_API_URL=http://localhost:8080
```

---

## Principais endpoints

**Auth**
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

**Clientes (Bearer)**
- `GET /clients?cep=...&name=...`
- `GET /clients/{id}`
- `POST /clients`
- `PUT /clients/{id}`
- `DELETE /clients/{id}`

**CEP (Bearer)**
- `GET /cep/{cep}`
