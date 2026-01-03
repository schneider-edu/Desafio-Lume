#!/usr/bin/env bash
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"

USER_EMAIL_1="${USER_EMAIL_1:-admin@lume.dev}"
USER_PASS_1="${USER_PASS_1:-senha123}"

USER_EMAIL_2="${USER_EMAIL_2:-user@lume.dev}"
USER_PASS_2="${USER_PASS_2:-senha123}"

CEP_DEFAULT="${CEP_DEFAULT:-01001000}"

has() { command -v "$1" >/dev/null 2>&1; }

json_get() {
  # Lê JSON do stdin e extrai a chave informada
  local key="$1"
  if has jq; then
    jq -r ".$key"
  elif has node; then
    node -e "const fs=require('fs'); const o=JSON.parse(fs.readFileSync(0,'utf8')); process.stdout.write(String(o['$key'] ?? ''))"
  else
    # fallback simples: funciona para JSON compacto (como o do backend)
    tr -d '\n' | sed -n "s/.*\"$key\":\"\\([^\"]*\\)\".*/\\1/p"
  fi
}

curl_json() {
  local method="$1"
  local path="$2"
  local body="${3:-}"
  local auth="${4:-}"

  local tmp
  tmp="$(mktemp)"

  local args=(-sS -o "$tmp" -w "%{http_code}" -H "Content-Type: application/json" -X "$method" "${API_URL}${path}")
  if [[ -n "$auth" ]]; then
    args+=(-H "Authorization: Bearer $auth")
  fi
  if [[ -n "$body" ]]; then
    args+=(-d "$body")
  fi

  local status
  status="$(curl "${args[@]}")"
  local resp
  resp="$(cat "$tmp")"
  rm -f "$tmp"

  echo "$status"
  echo "$resp"
}

echo "==> Registrando usuários (ignora se já existir)..."
read -r st body < <(curl_json POST "/auth/register" "{\"email\":\"${USER_EMAIL_1}\",\"password\":\"${USER_PASS_1}\"}" | awk 'NR==1{print $1} NR==2{print $0; exit}')
if [[ "$st" == "201" || "$st" == "409" ]]; then
  echo " - ${USER_EMAIL_1}: ok (status $st)"
else
  echo " - ${USER_EMAIL_1}: falhou (status $st): $body"
  exit 1
fi

read -r st body < <(curl_json POST "/auth/register" "{\"email\":\"${USER_EMAIL_2}\",\"password\":\"${USER_PASS_2}\"}" | awk 'NR==1{print $1} NR==2{print $0; exit}')
if [[ "$st" == "201" || "$st" == "409" ]]; then
  echo " - ${USER_EMAIL_2}: ok (status $st)"
else
  echo " - ${USER_EMAIL_2}: falhou (status $st): $body"
  exit 1
fi

echo
echo "==> Fazendo login para obter tokens..."
login_out="$(curl_json POST "/auth/login" "{\"email\":\"${USER_EMAIL_1}\",\"password\":\"${USER_PASS_1}\"}")"
st="$(echo "$login_out" | sed -n '1p')"
body="$(echo "$login_out" | sed -n '2,$p')"

if [[ "$st" != "200" ]]; then
  echo "Login falhou (status $st): $body"
  exit 1
fi

ACCESS_TOKEN="$(echo "$body" | json_get accessToken)"
REFRESH_TOKEN="$(echo "$body" | json_get refreshToken)"

if [[ -z "$ACCESS_TOKEN" || -z "$REFRESH_TOKEN" ]]; then
  echo "Falha ao extrair tokens do JSON. Resposta:"
  echo "$body"
  echo
  echo "Dica: instale jq (recomendado) ou rode em um terminal com Node disponível."
  exit 1
fi

echo " - accessToken OK (len=${#ACCESS_TOKEN})"
echo " - refreshToken OK (len=${#REFRESH_TOKEN})"

echo
echo "==> (Opcional) Testando CEP no backend..."
cep_out="$(curl_json GET "/cep/${CEP_DEFAULT}" "" "$ACCESS_TOKEN")"
st="$(echo "$cep_out" | sed -n '1p')"
body="$(echo "$cep_out" | sed -n '2,$p')"
if [[ "$st" == "200" ]]; then
  echo " - CEP ${CEP_DEFAULT} OK: $body"
else
  echo " - CEP ${CEP_DEFAULT} falhou (status $st): $body"
fi

echo
echo "==> Criando clientes..."
CPFS=(
  "12632948207"
  "25070591681"
  "33543178172"
  "56592694009"
  "61238313728"
  "61248568672"
  "61274332664"
  "68801867425"
  "82472514859"
  "89595908860"
)

i=1
for cpf in "${CPFS[@]}"; do
  payload="$(cat <<JSON
{"name":"Cliente ${i}","cpf":"${cpf}","cep":"${CEP_DEFAULT}","numero":"$((100+i))","complemento":"Seed"}
JSON
)"
  out="$(curl_json POST "/clients" "$payload" "$ACCESS_TOKEN")"
  st="$(echo "$out" | sed -n '1p')"
  body="$(echo "$out" | sed -n '2,$p')"

  if [[ "$st" == "201" ]]; then
    echo " - Cliente ${i} criado (cpf ${cpf})"
  elif [[ "$st" == "409" ]]; then
    echo " - Cliente ${i} já existe (cpf ${cpf})"
  else
    echo " - Cliente ${i} falhou (status $st): $body"
  fi
  i=$((i+1))
done

echo
echo "==> Pronto."
echo "Login seed:"
echo "  email: ${USER_EMAIL_1}"
echo "  senha: ${USER_PASS_1}"
echo
echo "Refresh token (para testar /auth/refresh):"
echo "  ${REFRESH_TOKEN}"
