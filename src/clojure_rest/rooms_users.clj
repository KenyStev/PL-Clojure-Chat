(ns clojure-rest.rooms_users
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
  	[clojure.java.jdbc :as sql]
  )
)

(defn get-all-rooms-users []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from rooms_users"]
        (into [] results)
      )
    )
  )
)

(defn get-users-for-room [room]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select u.email, u.username, u.realname 
            from users u inner join rooms_users ru
              on u.email = ru.user_id 
                inner join rooms r on ru.room_id = r.name
                  where r.name = ?" room]
        (into [] results)
      )
    )
  )
)

(defn get-room-user [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from rooms_users where id = ?" id]
      (cond
        (empty? results) {:status 404 :body "rooms_users not found"}
        :else (response (first results))
      )
    )
  )
)

(defn create-new-room-user [room-user] ;validar si ya esta agregado
  (let [id (uuid)]
  	(println (get room-user "user_id"))
    (println (get room-user "room_id"))
    (println (get room-user "is_admin"))
    (sql/with-connection (db-connection)
      (let [n_room-user (assoc room-user "id" id)]
        (sql/insert-record :rooms_users n_room-user)
      )
      (get-room-user id)
    )
  )
)

(defn update-room-user [id room-user]
  (sql/with-connection (db-connection)
    (let [u_room-user (assoc room-user "id" id)]
      (sql/update-values :rooms_users ["id=?" id] u_room-user)
    )
  )
  (get-room-user id)
)

(defn delete-room-user [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :rooms_users ["id=?" id])
  )
  {:status 204}
)