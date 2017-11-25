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

(deftest test-get-by-identifier
  (testing "Test getting one entry by an identifier one key"
    (let [result (perform-op :get
                             {:operation  :get
                              :connection {:impl (zdb/new-in-memory)}
                              :entity     :table-0
                              :identifier {:created-at 0}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (get-in result [:data 0 :other-val]) 4))
      (is (= (:count result) 1))))
  (testing "Test getting one entry by an identifier w/ multiple keys"
    (let [result (perform-op :get
                             {:operation  :get
                              :connection {:impl (zdb/new-in-memory)}
                              :entity     :table-0
                              :identifier {:updated-at 1
                                           :created-at 1}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (get-in result [:data 0 :other-val]) 7))
      (is (= (:count result) 1))))
  (testing "Test getting multiple entries by an identifier w/ multiple keys"
    (let [result (perform-op :get
                             {:operation  :get
                              :connection {:impl (zdb/new-in-memory)}
                              :entity     :table-0
                              :identifier {:text       "foo"
                                           :updated-at 1}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (:count result) 2))))
  (testing "Test getting no results for non-matching identifier clause"
    (let [result (perform-op :get
                             {:operation  :get
                              :connection {:impl (zdb/new-in-memory)}
                              :entity     :table-0
                              :identifier {:updated-at 10}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (zero? (:count result))))))
