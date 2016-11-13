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
                [compojure.route :as route]))

    

    (if (not (.exists (as-file "db/clojurechat.h2.db") ))
    (sql/with-connection (db-connection)
      ;(sql/drop-table :documents) ; no need to do that for in-memory databases
      (sql/create-table :documents [:id "varchar(256)" "primary key"]
                                   [:title "varchar(1024)"]
                                   [:text :varchar])))

    (defn get-all-documents []
      (response
        (sql/with-connection (db-connection)
          (sql/with-query-results results
            ["select * from documents"]
            (into [] results)))))

    (defn get-document [id]
      (sql/with-connection (db-connection)
        (sql/with-query-results results
          ["select * from documents where id = ?" id]
          (cond
            (empty? results) {:status 404}
            :else (response (first results))))))

    (defn create-new-document [doc]
      (let [id (uuid)]
        (sql/with-connection (db-connection)
          (let [document (assoc doc "id" id)]
            (sql/insert-record :documents document)))
        (get-document id)))

    (defn update-document [id doc]
        (sql/with-connection (db-connection)
          (let [document (assoc doc "id" id)]
            (sql/update-values :documents ["id=?" id] document)))
        (get-document id))

    (defn delete-document [id]
      (sql/with-connection (db-connection)
        (sql/delete-rows :documents ["id=?" id]))
      {:status 204})

    (defroutes app-routes
      (GET "/" [] "Clojure Rest-API by Nexer Rodriguez")
      (mp/wrap-multipart-params 
      (POST "/" {params :params} (upload-file (get params "file"))))
      (context "/users" [] (defroutes users-routes
        (GET "/" [] (get-all-users))
        (POST "/" {body :body} (create-new-user body))
        (context "/:id" [id] (defroutes users-routes
          (GET "/" [] (get-user id))
          (PUT "/" {body :body} (update-user body))
          (DELETE "/" [] (delete-user id))
          ))
        ))
      (context "/documents" [] (defroutes documents-routes
        (GET  "/" [] (get-all-documents))
        (POST "/" {body :body} (create-new-document body))
        (context "/:id" [id] (defroutes document-routes
          (GET    "/" [] (get-document id))
          (PUT    "/" {body :body} (update-document id body))
          (DELETE "/" [] (delete-document id))
          ))
        ))
      (route/not-found "Not Found"))

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))