(ns zaum.core-test
  (:require [clojure.test :refer :all]
            [zaum.core :refer :all]
            [zaum.databases.zaum :as zdb]))

(deftest test-get-all
  (testing "Basic test of getting all records in a table"
    (let [result (perform-op :get
                             {:operation  :get
                              :connection {:impl (zdb/new-in-memory)}
                              :entity     :table-0})]
      (is (= :ok (:status result)))
      (is (= (:count result) (count (:data result)))))))
