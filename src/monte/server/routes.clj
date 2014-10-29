(ns monte.server.routes
            (:require [monte.server.layout :as layout]
                      [monte.server.util :as util]
                      [compojure.core :refer :all]
                      [noir.response :refer [edn]]
                      [clojure.pprint :refer [pprint]]
                      [monte.server.status :as status]))

(defn home-page []
      (layout/render
        "app.html" 
        {}; {:docs (util/md->html "/md/docs.md")}

        ))

(defn save-document [doc]
      (pprint doc)
      {:status "ok"})

(defn old-page [] 
 (layout/render
        "monte_old.html"))

(defn status-page []
  (layout/render
     "app.html"
     {:status (status/get-status)})
  )


(defroutes monte-routes
  (GET "/" [] (home-page))
  (GET "/tmp" [] (old-page))
  

  ;(POST "/save" {:keys [body-params]}
  ;  (edn (save-document body-params)))


  )
