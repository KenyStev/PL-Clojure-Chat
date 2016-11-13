(ns clojure-rest.users
      (:import com.mchange.v2.c3p0.ComboPooledDataSource)
      (:use ring.util.response)
      (:use clojure.java.io)
      (:require 
      	[clojure-rest.db_config :refer :all]
      	[clojure.java.jdbc :as sql]
      	))

    (if (not (.exists (as-file "db/clojurechat.h2.db") ))
    (sql/with-connection (db-connection)
      ;(sql/drop-table :users) ; no need to do that for in-memory databases
      (sql/create-table :users [:id "varchar(256)" "primary key"]
                                   [:email "varchar(150)"]
                                   [:username :varchar]
                                   [:password :varchar]
                                   [:realname :varchar]
                                   )))

    (defn get-all-users []
      (response
        (sql/with-connection (db-connection)
          (sql/with-query-results results
            ["select * from users"]
            (into [] results)))))

    (defn get-user [id]
      (sql/with-connection (db-connection)
        (sql/with-query-results results
          ["select * from users where id = ?" id]
          (cond
            (empty? results) {:status 404}
            :else (response (first results))))))

    (defn create-new-user [user]
      (let [id (uuid)]
        (sql/with-connection (db-connection)
          (let [n_user (assoc user "id" id)]
            (sql/insert-record :users n_user)))
        (get-user id)))

    (defn update-user [id user]
        (sql/with-connection (db-connection)
          (let [u_user (assoc user "id" id)]
            (sql/update-values :users ["id=?" id] u_user)))
        (get-user id))

    (defn delete-user [id]
      (sql/with-connection (db-connection)
        (sql/delete-rows :users ["id=?" id]))
      {:status 204})

