(ns clojure-rest.friends
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
  	[clojure.java.jdbc :as sql]
  )
)

(defn get-all-friends [user_email]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select f.id as relation_id, u.email, u.username, u.realname from users u inner join friends f on u.id = f.user_id1  where user_id1 = ? " user_email]
        (into [] results)
      )
    )
  )
)

(defn get-friend [email]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select f.id as friend_id, u.email, u.username, u.realname from users u inner join friends f on u.email = f.user_id1  where u.email = ? " email]
      (cond
        (empty? results) {:status 404 :body "Friend not found"}
        :else (response (first results))
      )
    )
  )
)

(defn create-new-friend [friend headers]
  (let [id (uuid)]
    (sql/with-connection (db-connection)
      (let [n_friend (assoc friend "id" id)]
        (sql/insert-record :friends n_friend)
      )
      (get-friend id )
    )
  )
)

(defn delete-friend [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :friends ["id=?" id])
  )
  {:status 204}
)

