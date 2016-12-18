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
        (let [n_user (assoc user :profile_picture "db/profilePictures/user.png")]
          (sql/insert-record :users n_user)
          (get-user (get n_user "email"))
        )
      )

      {:status 500 :body "User already exist!"}
    )
  )
)

(defn update-user [email user]
  (sql/with-connection (db-connection)
    ; (let [u_user (assoc user "email" email)]
      (sql/update-values :users ["email=?" email] user)
    ; )
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
        ["select * from (select u.email as email, u.realname, 'user' as type from users u inner join messages m on m.to_who = u.email and m.from_who = ? or m.from_who = u.email and m.to_who = ? group by u.realname) gu union (select r.name as email, r.name as realname, 'room' as type 
          from rooms r 
            inner join rooms_users ru on ru.room_id = r.name
              inner join users u on ru.user_id = u.email where ru.user_id = ?)" from-who from-who from-who]
        ; ["select * from (select u.email as email, u.realname, 'user' as type from users u inner join messages m on m.to_who = u.email and m.from_who = ? or m.from_who = u.email and m.to_who = ? group by u.realname) gu union (select r.name as email, r.name as realname, 'room' as type 
        ;   from rooms r 
        ;     inner join rooms_users ru on ru.room_id = r.name
        ;       inner join users u on ru.user_id = u.email
        ;         inner join messages m on m.from_who = u.email and m.to_who = ?
        ;           or m.to_who = u.email and m.from_who = ?)" from-who from-who from-who from-who]
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

(defn get-profile-picture [user_email]
  (let [usr (get (get-user user_email) :body)]
    (println "email: " user_email)
    (println "usr: " usr)
    (get usr :profile_picture)
  )
)