(ns zaum.core-test
  (:require [clojure.test :refer :all]
            [zaum.core :refer :all]
            [zaum.databases.zaum :as zdb]))

(def test-data
  {:table-0
   [{:created-at 0 :updated-at 1 :text "foo" :other-val 4}
    {:created-at 1 :updated-at 1 :text "foo" :other-val 7}
    {:created-at 2 :updated-at 2 :text "bar" :other-val 5}
    {:created-at 3 :updated-at 3 :text "baz" :other-val 6}]})

(deftest test-read-all
  (testing "Basic test of getting all records in a table"
    (let [result (process-command {:operation  :read
                                   :connection {:impl
                                                (zdb/new-in-memory test-data)}
                                   :entity     :table-0})]
      (is (= :ok (:status result)))
      (is (= (:count result) (count (:data result)))))))

(deftest test-read-by-identifier
  (testing "Test getting one entry by an identifier one key"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:created-at 0}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (get-in result [:data 0 :other-val]) 4))
      (is (= (:count result) 1))))
  (testing "Test getting one entry by an identifier w/ multiple keys"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at 1
                                :created-at 1}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (get-in result [:data 0 :other-val]) 7))
      (is (= (:count result) 1))))
  (testing "Test getting multiple entries by an identifier w/ multiple keys"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:text       "foo"
                                :updated-at 1}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (= (:count result) 2))))
  (testing "Test getting no results for non-matching identifier clause"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at 10}})]
      (is (= :ok (:status result)))
      (is (= (:count result)) (count (:data result)))
      (is (zero? (:count result))))))

(deftest test-greater-than
  (testing "test basic > performance in memory"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at [> 1]}})]
      (is (= :ok (:status result)))
      (is (= (:count result) 2))
      (is (= (:count result) (count (:data result))))
      (is (= "bar" (-> result :data first :text)))
      (is (= "baz" (-> result :data second :text)))))
  (testing "test basic :> performance in memory"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at [:> 1]}})]
      (is (= :ok (:status result)))
      (is (= (:count result) 2))
      (is (= (:count result) (count (:data result))))
      (is (= "bar" (-> result :data first :text)))
      (is (= "baz" (-> result :data second :text)))))
  (testing "test :> performance in memory returning nothing"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at [:> 3]}})]
      (is (= :ok (:status result)))
      (is (zero? (:count result)))
      (is (= (:count result) (count (:data result))))))
  (testing "test :> performance in memory returning across two keys"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl (zdb/new-in-memory test-data)}
                   :entity     :table-0
                   :identifier {:updated-at [:> 1]
                                :other-val  [:> 5]}})]
      (is (= :ok (:status result)))
      (is (= (:count result) 1))
      (is (= (:count result) (count (:data result))))
      (is (= "baz" (-> result :data first :text))))))

(deftest test-read-from-missing-table
  (testing "Basic read on a nonexistent table"
    (let [result (process-command
                  {:operation  :read
                   :connection {:impl
                                (zdb/new-in-memory test-data)}
                   :entity     :table-1})]
      (is (= :warning (:status result)))
      (is (= "The table :table-1 does not exist.") (:message result))
      (is (zero? (:count result)))
      (is (= (:count result) (count (:data result)))))))

(deftest test-create-table
  (testing "Basic test of building a table"
    (let [con    {:impl (zdb/new-in-memory {})}
          create (process-command {:operation  :create
                                   :connection con
                                   :level      :table
                                   :entity     :table-0})
          read   (process-command {:operation  :read
                                   :connection con
                                   :entity     :table-0})]
      (is (= :ok (:status create)))
      (is (= 1 (:count create)))
      (is (= (:count create) (count (:data create))))
      (is (= "Table :table-0 created." (:message create)))
      (is (= :ok (:status read)))
      (is (zero? (:count read))))))
