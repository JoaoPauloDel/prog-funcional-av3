# backend

API REST da Calculadora de Calorias feita com Clojure, Compojure, Ring e JSON.
As calorias dos alimentos sao consultadas na API USDA FoodData Central.

## Prerequisitos

Antes de executar, tenha instalado:

- Java JDK.
- Leiningen 2.0.0 ou superior.
- Chave da API USDA FoodData Central.

Configure a chave antes de registrar alimentos:

```bash
USDA_API_KEY=sua_chave_aqui
```

## Como Executar

Entre na pasta do backend e inicie a API:

```bash
cd backend
lein ring server
```

Opcional: use `lein ring server-headless` se preferir que ele nao tente abrir um navegador web vazio.

O arquivo dos endpoints fica em `src/backend/handler.clj`.

## Endpoints

- `GET /`
- `POST /usuarios`
- `GET /usuarios/:id`
- `POST /alimentos`
- `POST /atividades`
- `GET /extrato?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
- `GET /saldo?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`

## Como Testar

```bash
lein test
```
