(ns backend.handler
  (:require [clj-http.client :as http]
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
  (System/getenv "USDA_API_KEY"))

(defn consultar-usda [parametros]
  (if-let [chave (api-key)]
    (:body (http/get "https://api.nal.usda.gov/fdc/v1/foods/search"
                     {:query-params (assoc parametros :api_key chave)
                          :as :json}))
    (throw (Exception. "USDA_API_KEY nao configurada"))))

(defn nutriente-energia? [nutriente]
  (and (= "Energy" (:nutrientName nutriente))
       (= "KCAL" (:unitName nutriente))))

(defn calorias-por-100g [alimento-usda]
  (or (:value (first (filter nutriente-energia?
                             (:foodNutrients alimento-usda))))
      0))

(defn calorias-alimento [alimento quantidade]
  (let [resposta (consultar-usda {:query alimento
                                  :pageSize 1})
        alimento-usda (first (:foods resposta))
        kcal-100g (calorias-por-100g alimento-usda)]
    (/ (* kcal-100g (para-numero quantidade)) 100)))

(defn calorias-atividade [calorias]
  (para-numero calorias))

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

(defn registrar-atividade! [{:keys [calorias] :as dados}]
  (registrar-transacao! (assoc dados :tipo "perda")
                        (- (calorias-atividade calorias))))

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
