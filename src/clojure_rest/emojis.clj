(ns clojure-rest.emojis
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
  	[clojure.java.jdbc :as sql]
  )
)

(defn get-all-emojis []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from emojis"]
        (into [] results)
      )
    )
  )
)

(defn get-emoji [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from emojis where id = ?" id]
      (cond
        (empty? results) {:status 404 :body "Emoji not found"}
        :else (response (first results))
      )
    )
  )
)

(defn create-new-emoji [emoji headers]
  (let [id (uuid)]
    (sql/with-connection (db-connection)
      (let [n_emoji (assoc emoji "id" id)]
        (sql/insert-record :users n_emoji)
      )
    )
    (get-emoji id)
  )
)

(defn update-emoji [id emoji]
  (sql/with-connection (db-connection)
    (let [u_emoji (assoc emoji "id" id)]
      (sql/update-values :emojis ["id=?" id] u_emoji)
    )
  )
  (get-emoji id)
)

(defn delete-emoji [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :emojis ["id=?" id])
  )
  {:status 204}
)