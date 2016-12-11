(use 'clojure-rest.db_setup)
(ns clojure-rest.handler
  (:use cheshire.core)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:use compojure.core)
  (:require 
    [compojure.handler :as handler]
    [ring.middleware.json :as middleware]
    [clojure.java.jdbc :as sql]
    [clojure-rest.users :refer :all]
    [ring.middleware [multipart-params :as mp]]
    [clojure-rest.db_config :refer :all]
    [clojure-rest.file_upload :refer :all]
    [clojure-rest.friends :refer :all]
    [clojure-rest.emojis :refer :all]
    [clojure-rest.messages :refer :all]
    [compojure.route :as route]
    [ring.middleware.cors :refer [wrap-cors]]
  )
)

(defroutes app-routes
  (GET "/" [] "Clojure Rest-API by Nexer Rodriguez and Kevin Estevez")
  (mp/wrap-multipart-params 
  (POST "/" {params :params} (upload-file (get params "file"))))
  (context "/users" [] (defroutes user-routes
    (GET "/" [] (get-all-users))
    (POST "/" {body :body headers :headers} (create-new-user body headers))
    (POST "/login" {body :body } (login body))
    (context "/:id" [id] (defroutes users-routes
      (GET "/" [] (get-user id))
      (PUT "/" {body :body} (update-user body))
      (DELETE "/" [] (delete-user id))
      ))
    )
  )
  (context "/friends" [] (defroutes friend-routes
    (GET "/:user_id" [user_id] (get-all-friends user_id))
    (POST "/" {body :body headers :headers} (create-new-friend body headers))
    (context "/:id" [id] (defroutes friends-routes
      (GET "/" [] (get-friend id))
      (DELETE "/" [] (delete-friend id))
      ))
    )
  )
  (context "/emojis" [] (defroutes emoji-routes
    (GET "/" [] (get-all-emojis))
    (POST "/" {body :body headers :headers} (create-new-emoji body headers))
    (context "/:id" [id] (defroutes emojis-routes
      (GET "/" [] (get-emoji id))
      (PUT "/" {body :body} (update-emoji body))
      (DELETE "/" [] (delete-emoji id))
      ))
    )
  )
  (context "/messages" [] (defroutes message-routes
    (GET "/" [] (get-all-messages))
    (GET "/between/:from-who/:to-who" [from-who to-who] (get-messages-between from-who to-who))
    (POST "/" {body :body} (create-new-message body))
    (context "/:id" [id] (defroutes messages-routes
      (GET "/" [] (get-message id))
      (PUT "/" {body :body} (update-message body))
      (DELETE "/" [] (delete-message id))
      ))
    )
  )
  (route/not-found "Not Found")
)

(def app
  (-> 
    (handler/api app-routes)
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-cors :access-control-allow-origin [#".*"])
  )
)