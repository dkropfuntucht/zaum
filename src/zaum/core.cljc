(ns zaum.core)

(defprotocol IZaumDatabase
  (perform-create [this command-map])
  (perform-read [this command-map]))

(defmulti perform-op (fn [op-key struct] op-key))

(defmethod perform-op :default
  [op-key struct]
  (throw
   (IllegalArgumentException. (str "Unknown Operation: " op-key))))

(defn current-time
  []
  #?(:cljs (system-time)
     :clj  (System/currentTimeMillis)))

(defn wrap-op
  [the-op]
  (let [st     (current-time)
        result (try
                 (the-op)
                 (catch Throwable t
                   {:status :error
                    :data   t}))
        et     (current-time)]
    (assoc result :time (- et st))))

(defmethod perform-op :read
  [op-key {:keys [connection] :as command}]
  (let [data (wrap-op #(perform-read (:impl connection) command))]
    (assoc data
           :result  :get
           :command command
           :count   (if (= :ok (:status data)) (count (:data data)) 0))))

(defmethod perform-op :create
  [op-key {:keys [connection] :as command}]
  (let [data (wrap-op #(perform-create (:impl connection) command))]
    (assoc data
           :result  :create
           :command command
           :count   (if (= :ok (:status data)) (count (:data data)) 0))))
