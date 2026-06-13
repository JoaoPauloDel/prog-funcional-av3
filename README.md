# Calculadora de Calorias

Projeto em Clojure dividido em duas aplicacoes:

- `backend`: API REST feita com Compojure, Ring e JSON.
- `frontend`: cliente CLI feito com o template `app` do Leiningen.

## Prerequisitos

Antes de executar o projeto, tenha instalado:

- Java JDK.
- Leiningen 2.0.0 ou superior.
- Chave da API USDA FoodData Central para consultar calorias dos alimentos.

## backend

API REST da Calculadora de Calorias feita com Clojure, Compojure, Ring e JSON. As calorias dos alimentos sao consultadas na API USDA FoodData Central.

Arquivo principal:

```text
backend/src/backend/handler.clj
```

Endpoints:

- `GET /`
- `POST /usuarios`
- `GET /usuarios/:id`
- `POST /alimentos`
- `POST /atividades`
- `GET /extrato?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
- `GET /saldo?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`

## frontend

Cliente CLI da Calculadora de Calorias feito com o template `app` do Leiningen.

Arquivo principal:

```text
frontend/src/frontend/core.clj
```

## Como Executar

A arquitetura exige dois terminais distintos operando simultaneamente.

### Terminal 1 - Inicie o Servidor Backend

Abra a pasta do backend e inicie a API:

```bash
cd backend
lein ring server
```

Opcional: use `lein ring server-headless` se preferir que ele nao tente abrir um navegador web vazio.

```
