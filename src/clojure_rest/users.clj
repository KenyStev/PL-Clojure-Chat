(ns clojure-rest.users
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
  	[clojure.java.jdbc :as sql]
  )
)

(defn get-all-users []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from users"]
        (into [] results)
      )
    )
  )
)

(defn get-user [email]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from users where email = ?" email]
      (cond
        (empty? results) {:status 404 :body "User not found"}
        :else (response (first results))
      )
    )
  )
)

(defn exist? [email]
  (println "viendo si existe")
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from users where email = ?" email]
      (cond
        (empty? results) 0
        :else 1
      )
    )
  )
)

(defn create-new-user [user headers]
	(println (get user "realname"))
  (println (get headers "content-type"))
  (let [result (exist? (get user "email"))]
    (println result)
    (if (= result 0)
      (sql/with-connection (db-connection)
        (sql/insert-record :users user)
        (get-user (get user "email"))
      )

      {:status 500 :body "User already exist!"}
    )
  )
)

(defn update-user [email user]
  (sql/with-connection (db-connection)
    (let [u_user (assoc user "email" email)]
      (sql/update-values :users ["email=?" email] u_user)
    )
  )
  (get-user email)
)

(defn delete-user [email]
  (sql/with-connection (db-connection)
    (sql/delete-rows :users ["email=?" email])
  )
  {:status 204}
)

(defn get-chats [from-who]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select u.realname, u.email from users u inner join messages m on m.to_who = u.email and m.from_who = ?" from-who]
        (into [] results)
      )
    )
  )
)

(defn login [user]
  (sql/with-connection (db-connection)
    (sql/with-query-results results 
      ["select * from users where email = ? and password = ?" (get user "email") (get user "password")] 
      (cond (empty? results) {:status 404 :body "Email and Password don't match"}
        :else (response (first results))
      )
    )
  )
)