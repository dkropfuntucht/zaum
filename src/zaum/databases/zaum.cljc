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
  (perform-create [_ {:keys [entity level data]}]
    (cond  (and (= level :table) (contains? @store entity))
           ;;TODO: likely an error condition or should it be idempotent and 'clean'?
           ;; - we're considering just returning this as an error
           ;; - not sure if it shouldn't be idempotent - we'll know more later
           (throw (Exception. "Attempt to create duplicate table."))
           (= level :table)
           ;;TODO: this represents the 'table' - not sure these implementations
           ;; shouldn't be adapted to assoc and return the command message
           (do
             (swap! store #(assoc % entity []))
             ;; - for :data we return the empty table [] in a collection of "created" table(s)
             {:status :ok :data [[]] :message (str "Table " entity " created.")})
           :or
           (throw (Exception. "Unknown create operation"))))

  (perform-get [_ {:keys [entity identifier] :as command}]
    (cond
      (not (contains? @store entity))
      {:status :warning :data [] :message (str "The table " entity " does not exist.")}
      (nil? identifier)
      {:status :ok :data (@store entity)}
      (map? identifier)
      {:status :ok
       :data (vec (filter (construct-filter identifier) (@store entity)))})))

(defn new-in-memory
  ([]
   (ZaumInMemory. (atom {})))
  ([initial-data]
   (ZaumInMemory. (atom initial-data))))
