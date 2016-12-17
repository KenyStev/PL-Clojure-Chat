(ns clojure-rest.messages
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
  	[clojure.java.jdbc :as sql]
  )
)

(defn get-all-messages []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from messages"]
        (into [] results)
      )
    )
  )
)

(defn get-message [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from messages where id = ?" id]
      (cond
        (empty? results) {:status 404 :body "Message not found"}
        :else (response (first results))
      )
    )
  )
)

(defn create-new-message [message]
  (let [id (uuid)]
    (sql/with-connection (db-connection)
      (let [n_message (assoc message "id" id)]
        (sql/insert-record :messages n_message)
      )
    )
    (get-message id)
  )
)

(defn update-message [id message]
  (sql/with-connection (db-connection)
    (let [u_message (assoc message "id" id)]
      (sql/update-values :messages ["id=?" id] u_message)
    )
  )
  (get-message id)
)

(defn delete-message [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :messages ["id=?" id])
  )
  {:status 204}
)

(defn get-messages-between [from-who to-who]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from messages where from_who = ? and to_who = ? 
          or from_who = ? and to_who = ? order by sent desc" from-who to-who to-who from-who]
        (into [] results)
      )
    )
  )
)

(defn get-messages-from-room [to-which-room]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from messages where to_who = ? order by sent desc" to-which-room]
        (into [] results)
      )
    )
  )
)