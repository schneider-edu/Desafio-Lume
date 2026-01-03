# Desafio Técnico — Spring Boot (Java 21) + React

Este repositório contém:
- `backend/`: API Spring Boot (Java 21, Maven, H2, JPA, Security com JWT + Refresh Token, Swagger)
- `frontend/`: React (Vite + TS + MUI) com Login e CRUD de Clientes (com busca)

## Requisitos
- Java 21
- Maven 3.9+
- Node 18+ (ou 20+)

---

## Como rodar (modo local)

### 1) Backend
```bash
cd backend
mvn spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console (opcional): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:challenge`
  - user: `sa`
  - password: (vazio)

### 2) Frontend
```bash
cd frontend
npm i
npm run dev
```

- Frontend: `http://localhost:5173`

Por padrão, o frontend aponta para `http://localhost:8080`.
Se quiser mudar:
```bash
# frontend/.env
VITE_API_URL=http://localhost:8080
```

---

## Fluxo de uso (rápido)
1) Abra o Swagger e faça `POST /auth/register`
2) Faça `POST /auth/login` (retorna accessToken + refreshToken)
3) Use o botão **Authorize** (Bearer) no Swagger com o **accessToken**
4) Use endpoints de `/clients` e `/cep/{cep}`

No frontend:
- Faça login (o app salva tokens no `localStorage`)
- A tela de clientes permite buscar, criar, editar e excluir
- No formulário, use “Buscar CEP” para pré-preencher endereço (editável)

---

## Principais endpoints

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh` (rota refresh + rotação de refreshToken)
- `POST /auth/logout` (revoga refreshToken)

### Clientes (protegido)
- `GET /clients?query=...`
- `GET /clients/{id}`
- `POST /clients`
- `PUT /clients/{id}`
- `DELETE /clients/{id}`

### CEP (protegido)
- `GET /cep/{cep}`

---

## Docker (diferencial)
Inclui Dockerfiles e `docker-compose.yml`.

```bash
docker compose up --build
```

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:5173`
