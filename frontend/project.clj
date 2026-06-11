(defproject frontend "0.1.0-SNAPSHOT"
  :description "Cliente CLI para calculadora de calorias"
  :url "https://github.com"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [clj-http "3.13.0"]
                 [cheshire "5.13.0"]]
  :main ^:skip-aot frontend.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all 
                        :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
