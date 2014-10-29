(ns monte.server.status
  (:require [monte.miners.core :as miners]))


(defn get-memory-status []
  (let [runtime (Runtime/getRuntime)
        free-mem (.freeMemory runtime)
        total-mem (.totalMemory runtime)
        max-mem (.maxMemory runtime)
        mb (* 1024 1024)]
    {:used (/ (-
                total-mem 
                free-mem) mb)
     :free  (/ free-mem mb)
     :total (/ total-mem mb)
     :max   (/ max-mem mb)}))


(defn get-status [] 
  ;(dbg (session/session-get :runtime))
  {;:workspace @workspace ; tbd: workspace is obsolete 
   ;:changes   @changes
   :monte {:memory (get-memory-status)}
   :miners (miners/m-list-meta)})

