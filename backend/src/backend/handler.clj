(ns backend.handler
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defonce banco (atom {:proximo-usuario 1
                      :proximo-transacao 1
                      :usuarios '()
                      :transacoes '()}))

(defn para-numero [valor]
  (cond
    (number? valor) valor
    (string? valor) (Double/parseDouble valor)
    :else 0))

(defn api-key []
  (System/getenv "API_NINJAS_KEY"))

(defn consultar-api-ninjas [url parametros]
  (if-let [chave (api-key)]
    (:body (http/get url {:query-params parametros
                          :headers {"X-Api-Key" chave}
                          :as :json}))
    (throw (Exception. "API_NINJAS_KEY nao configurada"))))

(defn calorias-alimento [alimento quantidade]
  (let [resposta (consultar-api-ninjas
                  "https://api.api-ninjas.com/v1/nutrition"
                  {:query (str quantidade " " alimento)})]
    (reduce + (map :calories resposta))))

(defn calorias-atividade [atividade duracao]
  (let [resposta (consultar-api-ninjas
                  "https://api.api-ninjas.com/v1/caloriesburned"
                  {:activity atividade
                   :duration duracao})]
    (reduce + (map :total_calories resposta))))

(defn dentro-do-periodo? [inicio fim transacao]
  (let [data (:data transacao)]
    (and (or (empty? inicio) (not (neg? (compare data inicio))))
         (or (empty? fim) (not (pos? (compare data fim)))))))

(defn extrato [inicio fim]
  (filter (partial dentro-do-periodo? inicio fim) (:transacoes @banco)))

(defn saldo [transacoes]
  (reduce + (map :calorias transacoes)))

(defn cadastrar-usuario! [dados]
  (let [usuario (assoc dados :id (:proximo-usuario @banco))]
    (swap! banco
           (fn [estado]
             (-> estado
                 (update :proximo-usuario inc)
                 (update :usuarios conj usuario))))
    usuario))

(defn consultar-usuario [id]
  (first (filter #(= (:id %) id) (:usuarios @banco))))

(defn registrar-transacao! [dados calorias]
  (let [transacao (assoc dados
                         :id (:proximo-transacao @banco)
                         :calorias calorias)]
    (swap! banco
           (fn [estado]
             (-> estado
                 (update :proximo-transacao inc)
                 (update :transacoes conj transacao))))
    transacao))

(defn registrar-alimento! [{:keys [alimento quantidade] :as dados}]
  (registrar-transacao! (assoc dados :tipo "ganho")
                        (calorias-alimento alimento quantidade)))

(defn registrar-atividade! [{:keys [atividade duracao] :as dados}]
  (registrar-transacao! (assoc dados :tipo "perda")
                        (- (calorias-atividade atividade duracao))))

(defn resposta [status corpo]
  {:status status
   :body corpo})

(defn erro-api [erro]
  (resposta 502 {:erro (.getMessage erro)}))

(defroutes app-routes
  (GET "/" []
    (resposta 200 {:mensagem "API da Calculadora de Calorias"
                   :rotas ["/usuarios"
                           "/usuarios/:id"
                           "/alimentos"
                           "/atividades"
                           "/extrato"
                           "/saldo"]}))

  (POST "/usuarios" {dados :body}
    (resposta 201 (cadastrar-usuario! dados)))

  (GET "/usuarios/:id" [id]
    (if-let [usuario (consultar-usuario (Integer/parseInt id))]
      (resposta 200 usuario)
      (resposta 404 {:erro "Usuario nao encontrado"})))

  (POST "/alimentos" {dados :body}
    (try
      (resposta 201 (registrar-alimento! dados))
      (catch Exception erro
        (erro-api erro))))

  (POST "/atividades" {dados :body}
    (try
      (resposta 201 (registrar-atividade! dados))
      (catch Exception erro
        (erro-api erro))))

  (GET "/extrato" [inicio fim]
    (resposta 200 (extrato inicio fim)))

  (GET "/saldo" [inicio fim]
    (let [transacoes (extrato inicio fim)]
      (resposta 200 {:saldo (saldo transacoes)})))

  (route/not-found (resposta 404 {:erro "Rota nao encontrada"})))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))
