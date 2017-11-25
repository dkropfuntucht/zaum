(ns zaum.core)

(defprotocol IZaumDatabase
  (perform-get [this command-map]))

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
  [fn]
  (let [st     (current-time)
        result (try {:status :ok :data (fn)}
                    (catch Throwable t {:status :error :data t}))
        et     (current-time)]
    (assoc result :time (- et st))))

(defmethod perform-op :get
  [op-key {:keys [connection] :as commands}]
  (let [data (wrap-op #(perform-get (:impl connection) commands))]
    (assoc data
           :result  :get
           :command commands
           :count   (count (:data data)))))
