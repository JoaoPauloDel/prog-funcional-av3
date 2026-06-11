(ns frontend.core
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.string :as str])
  (:gen-class))

(def api-url "http://localhost:3000")

(defn ler [mensagem]
  (print mensagem)
  (flush)
  (read-line))

(defn ler-numero [mensagem]
  (Double/parseDouble (ler mensagem)))

(defn dados-usuario []
  {:altura (ler-numero "Altura em cm: ")
   :peso (ler-numero "Peso em kg: ")
   :idade (ler-numero "Idade: ")
   :sexo (ler "Sexo: ")})

(defn dados-alimento []
  {:usuario-id (ler-numero "Id do usuario: ")
   :data (ler "Data (aaaa-mm-dd): ")
   :alimento (ler "Alimento consumido: ")
   :quantidade (ler "Quantidade consumida: ")})

(defn dados-atividade []
  {:usuario-id (ler-numero "Id do usuario: ")
   :data (ler "Data (aaaa-mm-dd): ")
   :atividade (ler "Atividade fisica: ")
   :duracao (ler-numero "Duracao em minutos: ")})

(defn post-json [rota corpo]
  (:body (http/post (str api-url rota)
                    {:body (json/generate-string corpo)
                     :content-type :json
                     :accept :json
                     :as :json})))

(defn get-json [rota]
  (:body (http/get (str api-url rota) {:as :json})))

(defn mostrar-transacao [{:keys [id tipo data calorias]}]
  (println (format "#%s | %s | %s | %s kcal" id tipo data calorias)))

(defn cadastrar-usuario! []
  (println "\nCadastro de usuario")
  (println (post-json "/usuarios" (dados-usuario))))

(defn registrar-alimento! []
  (println "\nConsumo de alimento")
  (println (post-json "/alimentos" (dados-alimento))))

(defn registrar-atividade! []
  (println "\nAtividade fisica")
  (println (post-json "/atividades" (dados-atividade))))

(defn mostrar-transacoes [transacoes]
  (when (seq transacoes)
    (mostrar-transacao (first transacoes))
    (recur (rest transacoes))))

(defn periodo []
  {:inicio (ler "Data inicial (aaaa-mm-dd): ")
   :fim (ler "Data final (aaaa-mm-dd): ")})

(defn extrato! []
  (let [{:keys [inicio fim]} (periodo)]
    (mostrar-transacoes (get-json (str "/extrato?inicio=" inicio "&fim=" fim)))))

(defn saldo! []
  (let [{:keys [inicio fim]} (periodo)]
    (println (get-json (str "/saldo?inicio=" inicio "&fim=" fim)))))

(defn menu []
  (println "\nCalculadora de Calorias")
  (println "1 - Cadastrar usuario")
  (println "2 - Registrar alimento")
  (println "3 - Registrar atividade fisica")
  (println "4 - Consultar extrato")
  (println "5 - Consultar saldo")
  (println "0 - Sair")
  (ler "Escolha: "))

(defn executar-opcao [opcao]
  (case opcao
    "1" (cadastrar-usuario!)
    "2" (registrar-alimento!)
    "3" (registrar-atividade!)
    "4" (extrato!)
    "5" (saldo!)
    "0" (println "Ate mais!")
    (println "Opcao invalida.")))

(defn executar-menu []
  (let [opcao (menu)]
    (executar-opcao opcao)
    (when-not (= opcao "0")
      (recur))))

(defn -main [& _args]
  (executar-menu))
