(ns marauder-api.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [resources]]
            [environ.core :refer [env]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [assoc-query-params wrap-params]]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [org.httpkit.server :refer [run-server close]]))

(def rotunda-coords [38.035717 -78.503355])

(def profiles*
  (atom
   {"pettigrew@b.com" {:email "pettigrew@b.com"
                       :nickname "wormtail"
                       :coordinate rotunda-coords
                       :icon 3}}))

(defn update-handler [{:keys [email nickname coordinate icon] :as profile}]
  (prn "Update handler")
  (swap! profiles* assoc email profile)
  {:status 200 :body {:profiles (vals @profiles*)}})

(defn lock-handler [email]
  (prn "Lock handler")
  (let [profiles @profiles*
        removed (remove #(= (:email %) email) profiles)]
    (reset! profiles* removed)))

(defroutes handler
  (POST "/update" [profile :as request]
        (update-handler profile))
  (POST "/lock" [email]
        (lock-handler email)))

(def app (-> handler
             wrap-keyword-params
             wrap-params
             wrap-json-params
             wrap-json-response))

(defn -main []
  (let [port (or (env :port) 8085)]
    (prn "Main running on port " port)
    (run-server app {:port port})))
