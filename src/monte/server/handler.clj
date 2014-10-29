(ns monte.server.handler
  (:use ring.server.standalone
        [ring.middleware file-info file])
  (:require [compojure.core :refer [defroutes]]
            [monte.server.routes :refer [monte-routes]]
            [monte.server.middleware :refer [load-middleware]]
            [monte.server.session-manager :as session-manager]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]
            ))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when app is deployed as a servlet on
   an app server such as Tomcat put any initialization code here"
  []
  (timbre/set-config! [:appenders :rotor] {:min-level :info
                                           :enabled? true
                                           :async? false ; should be always false for rotor
                                           :max-message-per-msecs nil
                                           :fn rotor/appender-fn})
  (timbre/set-config! [:shared-appender-config :rotor]
                      {:path "monte.server.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session-manager/cleanup-job)
  (timbre/info "\n-=[ monte.server started successfully"
               (when (env :dev) "using the development profile") "]=-"))

(defn destroy
  "destroy will be called when your application shuts down, put any clean up code here"
  []
  (timbre/info "monte.server is shutting down...")
  (cronj/shutdown! session-manager/cleanup-job)
  (timbre/info "shutdown complete!"))


;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 60 30)
   :timeout-response (redirect "/")})


(defn- mk-defaults
       "set to true to enable XSS protection"
       [xss-protection?]
       (-> site-defaults
           (update-in [:session] merge session-defaults)
           (assoc-in [:security :xss-protection :enable?] xss-protection?)
           (assoc-in [:security :anti-forgery] xss-protection?)))


(defn gen-app-handler[& custom-routes]
  (println custom-routes)
  (app-handler
           ;; add your application routes here
           (if custom-routes
            (into (if (vector? custom-routes)
                           custom-routes
                           (vec custom-routes)) [monte-routes base-routes])
            [monte-routes base-routes])
           
           ;; add custom middleware here
           :middleware (load-middleware)
           :ring-defaults (mk-defaults false)
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn :transit-json]))

(def app (gen-app-handler))

;; from repl

(comment 
(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body
      (wrap-file-info)))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 3000)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join? false}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))
)

;; from runtime
