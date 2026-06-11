# backend

API REST da Calculadora de Calorias feita com Clojure, Compojure, Ring e JSON.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

The API exposes the main endpoints in `src/backend/handler.clj`.

## Endpoints

- `GET /`
- `POST /usuarios`
- `GET /usuarios/:id`
- `POST /alimentos`
- `POST /atividades`
- `GET /extrato?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
- `GET /saldo?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`
