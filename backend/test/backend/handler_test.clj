(ns backend.handler-test
  (:require [backend.handler :refer :all]
            [cheshire.core :as json]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(def usuario
  {:altura 180
   :peso 80
   :idade 30
   :sexo "masculino"})

(deftest funcoes-puras
  (testing "calcula saldo com reduce e map"
    (is (= 70 (saldo [{:calorias 100} {:calorias -30}]))))
  (testing "filtra extrato por periodo"
    (is (= '({:data "2026-06-10" :calorias 100})
           (filter (partial dentro-do-periodo? "2026-06-01" "2026-06-30")
                   '({:data "2026-05-10" :calorias 50}
                     {:data "2026-06-10" :calorias 100}))))))

(deftest rotas-da-api
  (reset! banco {:proximo-usuario 1
                 :proximo-transacao 1
                 :usuarios '()
                 :transacoes '()})
  (testing "rota inicial"
    (let [response (app (mock/request :get "/"))]
      (is (= 200 (:status response)))))
  (testing "cadastra e consulta usuario"
    (let [response (app (-> (mock/request :post "/usuarios")
                            (mock/json-body usuario)))
          corpo (json/parse-string (:body response) true)]
      (is (= 201 (:status response)))
      (is (= 1 (:id corpo)))))
  (testing "not found"
    (is (= 404 (:status (app (mock/request :get "/usuarios/999")))))))
