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
    [clojure-rest.rooms :refer :all]
    [clojure-rest.rooms_users :refer :all]
    [compojure.route :as route]
    [ring.middleware.cors :refer [wrap-cors]]
  )
)

(defroutes app-routes
  (GET "/" [] "Clojure Rest-API by Nexer Rodriguez and Kevin Estevez")
  
  (mp/wrap-multipart-params 
    (POST "/user/update-picture" {params :params}
      (println (str "email: " (get params "email")))
      (println (str "params: " params))
      (let [image (get params "profileImage")]
        (let [usr (get (get-user (get params "email")) :body)]
          (println "usr: " usr)
            (let [fileName (str (str (get usr :username) "/") (get image :filename))]
            (println (str "fileName: " fileName))
            (upload-file-to image fileName)
            (let [n_user (assoc usr :profile_picture (str "db/profilePictures/" fileName))] 
              ; (assoc usr "profilePicture" (str "db/profilePictures/" fileName))
              (println "n_user: " n_user)
              (update-user (get n_user :email) n_user)
              {:status 200 :body "profile picture has been updated"}
            )
          )
        )
      )
    )
    ; (POST "/" {params :params} (upload-file (get params "file")))
  )
  (GET "/profile-picture/:user_email" [user_email]
    (file-response
      (get-profile-picture user_email) {:root "./"}
    )
  )
  (context "/users" [] (defroutes user-routes
    (GET "/" [] (get-all-users))
    (POST "/" {body :body headers :headers} (create-new-user body headers))
    (POST "/login" {body :body } (login body))
    (context "/:id" [id] (defroutes users-routes
      (GET "/" [] (get-user id))
      (GET "/my-rooms" [] (get-my-rooms id))
      (GET "/rooms" [] (get-rooms-where-iam id))
      (PUT "/" {body :body} (update-user id body))
      (DELETE "/" [] (delete-user id))
      ))
    )
  )
  (context "/friends" [] (defroutes friend-routes
    (GET "/from/:user_email" [user_email] (get-all-friends user_email))
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
    (GET "/from/room/:to-which-room" [to-which-room] (get-messages-from-room to-which-room))
    (POST "/" {body :body} (create-new-message body))
    (context "/:id" [id] (defroutes messages-routes
      (GET "/" [] (get-message id))
      (PUT "/" {body :body} (update-message body))
      (DELETE "/" [] (delete-message id))
      ))
    )
  )
  (context "/rooms" [] (defroutes room-routes
    (GET "/" [] (get-all-rooms))
    (POST "/new" {body :body} (create-new-room (get body "admin") (get body "room")))
    (context "/:name_room" [name_room] (defroutes rooms-routes
      (GET "/" [] (get-room name_room))
      (GET "/all-users" [] (get-users-for-room name_room))
      (PUT "/" {body :body} (update-room name_room body))
      (DELETE "/" [] (delete-room name_room))
      ))
    )
  )
  (context "/rooms-users" [] (defroutes room-users-routes
    (GET "/" [] (get-all-rooms-users))
    (POST "/" {body :body} (create-new-room-user body))
    (context "/:id" [id] (defroutes rooms-users-routes
      (DELETE "/" [] (delete-room-user id))
      ))
    )
  )
  (GET "/chats/:user_id" [user_id] (get-chats user_id))
  (route/not-found "Not Found")
)

(def app
  (-> 
    (handler/api app-routes)
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-cors routes #".*")
  )
)