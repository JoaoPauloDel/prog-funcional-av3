# Relatorio Tecnico - Calculadora de Calorias

## Objetivo

O projeto implementa uma calculadora de calorias em Clojure dividida em dois programas, conforme o enunciado do projeto:

- `backend`: API REST responsavel por registrar dados pessoais, registrar transacoes de ganho/perda caloricos e guardar dados em memoria.
- `frontend`: cliente de terminal que apresenta um menu iterativo e se comunica com a API por HTTP.

## Arquitetura

```text
trab-av3/
├── backend/
│   ├── project.clj
│   └── src/backend/handler.clj
├── frontend/
│   ├── project.clj
│   └── src/frontend/core.clj
└── RELATORIO_TECNICO.md
```

O backend usa Compojure para definir rotas, Ring para receber requisicoes HTTP, `ring-json` para entrada e saida em JSON, `clj-http` para chamadas HTTP e `cheshire` para JSON, seguindo os capitulos 9 a 13 do livro.

## Funcionalidades

- Cadastro e consulta de dados pessoais: altura, peso, idade e sexo.
- Registro de consumo de alimento como ganho calorico usando a API USDA FoodData Central.
- Registro de atividade fisica como perda calorica.
- Consulta de extrato de transacoes por periodo.
- Consulta de saldo de calorias por periodo.
- Persistencia temporaria em memoria usando `atom`.
- Menu CLI com recursao de cauda usando `recur`.

## Uso de Programacao Funcional

As regras principais foram separadas em funcoes:

- `extrato`: filtra as transacoes por periodo.
- `saldo`: calcula o saldo a partir das transacoes.
- `dentro-do-periodo?`: verifica se uma transacao pertence ao periodo informado.

A funcao `saldo` usa `reduce` e `map` para acumular o total de calorias a partir da lista de transacoes. O cliente CLI usa recursao de cauda com `recur` para manter o menu ativo.

O estado mutavel ficou isolado no `atom` chamado `banco`. As transacoes sao representadas por mapas e armazenadas em listas.

## Rotas da API

- `GET /`: mostra uma mensagem inicial e as rotas disponiveis.
- `POST /usuarios`: registra dados pessoais.
- `GET /usuarios/:id`: consulta dados pessoais.
- `POST /alimentos`: registra consumo de alimento e consulta calorias na USDA FoodData Central.
- `POST /atividades`: registra realizacao de atividade fisica.
- `GET /extrato?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`: consulta extrato por periodo.
- `GET /saldo?inicio=aaaa-mm-dd&fim=aaaa-mm-dd`: consulta saldo por periodo.

## Exemplo de JSON para POST /usuarios

```json
{
  "altura": 165,
  "peso": 62,
  "idade": 25,
  "sexo": "feminino"
}
```

Para chamadas externas de calorias de alimentos usando USDA FoodData Central, configure a variavel `USDA_API_KEY`.

## Como Executar

Em um terminal:

```bash
cd backend
lein ring server-headless 3000
```

Em outro terminal:

```bash
cd frontend
lein run
```

## Como Testar

```bash
cd backend
lein test
```

```bash
cd frontend
lein test
```
