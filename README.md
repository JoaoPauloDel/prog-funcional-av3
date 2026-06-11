# backend

API REST da Calculadora de Calorias feita com Clojure, Compojure, Ring e JSON.
As calorias dos alimentos sao consultadas na API USDA FoodData Central.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

The API exposes the main endpoints in `src/backend/handler.clj`.
Configure `USDA_API_KEY` antes de registrar alimentos.

## Endpoints

- `GET /`
- `POST /usuarios`
- `GET /usuarios/:id`
- `POST /alimentos`
- `POST /atividades`
- `GET /extrato?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
- `GET /saldo?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
