(ns monte.server
	(:require [monte.server.handler :as handler]))

;;
;; api will be here
;;

(defn foo[]
	(println "Foo"))


(defn ring-handler 
	"generates a ring handler with monte support"
	[& custom-routes]
	(apply handler/gen-app-handler custom-routes))