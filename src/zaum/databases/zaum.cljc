(ns zaum.databases.zaum
  (:require [zaum.core :as z]))

(def test-data
  {:table-0
   [{:created-at 0 :updated-at 1 :text "foo" :other-val 4}
    {:created-at 1 :updated-at 1 :text "foo" :other-val 7}
    {:created-at 2 :updated-at 2 :text "bar" :other-val 5}
    {:created-at 3 :updated-at 3 :text "baz" :other-val 6}]})

(defn perform-get-impl [command-map])

(defn- construct-filter
  [identifier]
  (fn [v]
    (every?
     (fn [kv]
       (and (contains? v (key kv))
            (= ((key kv) v) (val kv))))
     identifier)))

(defrecord ZaumInMemory []
  z/IZaumDatabase
  (perform-get [_ {:keys [entity identifier]}]
    (cond
      (nil? identifier)
      (test-data entity)
      (map? identifier)
      (vec (filter (construct-filter identifier) (test-data entity))))))

(defn new-in-memory []
  (ZaumInMemory.))
