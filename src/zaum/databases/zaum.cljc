(ns zaum.databases.zaum
  (:require [zaum.core :as z]))

(defn perform-get-impl [command-map])

(def search-types
  { > >
   :> >})

(defn- construct-filter
  [identifier]
  (fn [v]
    (every?
     (fn [filter-spec]
       (let [[filter-key filter-search] filter-spec]
         (and (contains? v filter-key)
              (cond (vector? filter-search)
                    ((search-types (first filter-search))
                     (filter-key v)
                     (second filter-search))
                    :or
                    (= (filter-key v) filter-search)))))
     identifier)))

(defrecord ZaumInMemory [store]
  z/IZaumDatabase
  (perform-get [_ {:keys [entity identifier]}]
    (cond
      (nil? identifier)
      (@store entity)
      (map? identifier)
      (vec (filter (construct-filter identifier) (@store entity))))))

(defn new-in-memory
  ([]
   (ZaumInMemory. (atom {})))
  ([initial-data]
   (ZaumInMemory. (atom initial-data))))
