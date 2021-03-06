(ns marauder-api.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [resources]]
            [environ.core :refer [env]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
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
  (prn "Update handler: " profile)
  (if (every? some? [email nickname coordinate icon])
    (do (swap! profiles* assoc email profile)
        {:status 200 :body {:profiles (or (vals (dissoc @profiles* email)) [])}})
    (do (prn "Bad JSON data - profile not inserted")
        {:status 200 :body {:profiles (or (vals (dissoc @profiles* email)) [])}})))

(defn lock-handler [email]
  (prn "Lock handler: " email)
  (swap! profiles* dissoc email)
  {:status 200 :body "removed"})

(defroutes handler
  (GET "/debug" []
       (or (vals @profiles*) []))
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
  (let [port (Integer. (or (env :port) 8085))]
    (prn "Server running on port " port)
    (run-server app {:port port})))
