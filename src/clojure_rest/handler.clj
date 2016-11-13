(ns clojure-rest.handler
      (:use cheshire.core)
      (:use ring.util.response)
      (:use clojure.java.io)
      (:use compojure.core)
      (:require [compojure.handler :as handler]
                [ring.middleware.json :as middleware]
                [clojure.java.jdbc :as sql]
                [clojure-rest.users :refer :all]
                [ring.middleware [multipart-params :as mp]]
                [clojure-rest.db_config :refer :all]
                [clojure-rest.file_upload :refer :all]
                [clojure-rest.friends :refer :all]
                [compojure.route :as route]))

    
    (defroutes app-routes
      (GET "/" [] "Clojure Rest-API by Nexer Rodriguez")
      (mp/wrap-multipart-params 
      (POST "/" {params :params} (upload-file (get params "file"))))
      (context "/users" [] (defroutes users-routes
        (GET "/" [] (get-all-users))
        (POST "/" {body :body headers :headers} (create-new-user body headers))
        (context "/:id" [id] (defroutes users-routes
          (GET "/" [] (get-user id))
          (PUT "/" {body :body} (update-user body))
          (DELETE "/" [] (delete-user id))
          ))
        ))
      (context "/friends" [] (defroutes friend-routes
        (GET "/:user_id" [user_id] (get-all-friends user_id))
        (POST "/" {body :body headers :headers} (create-new-friend body headers))
        (context "/:id" [id] (defroutes friends-routes
          (GET "/" [] (get-friend id))
          (DELETE "/" [] (delete-friend id))
          ))
        ))
      (route/not-found "Not Found"))

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))