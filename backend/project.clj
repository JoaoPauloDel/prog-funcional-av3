(defproject backend "0.1.0-SNAPSHOT"
  :description "API REST para calculadora de calorias"
  :url "https://github.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [clj-http "3.13.0"]
                 [cheshire "5.13.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.1"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler backend.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
