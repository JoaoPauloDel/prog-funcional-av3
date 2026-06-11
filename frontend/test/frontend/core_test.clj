(ns frontend.core-test
  (:require [clojure.test :refer :all]
            [frontend.core :refer :all]))

(deftest formatacao
  (testing "monta URL base da API"
    (is (= "http://localhost:3000" api-url))))
