(ns clojure-rest.emojis
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use ring.util.response)
  (:use clojure.java.io)
  (:require 
  	[clojure-rest.db_config :refer :all]
    [clojure-rest.file_upload :refer :all]
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
(defn emoji-exist? [nameEmoji]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from emojis where name = ?" nameEmoji]
      (cond
        (empty? results) 0
        :else 1
      )
    )
  )
)

(defn get-emoji [nameEmoji]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from emojis where name = ?" nameEmoji]
      (cond
        (empty? results) {:status 404 :body {:image "resources/emojis/not-found.png" :msg "Emoji not found"} }
        :else (response (first results))
      )
    )
  )
)

(defn create-new-emoji [emoji]
  (let [e_name (get emoji "name")]
    (let [result (emoji-exist? e_name)]
      (if (= result 0)
        (sql/with-connection (db-connection)
          (sql/insert-record :emojis emoji)
          (get-emoji e_name)
        )

        {:status 500 :body "emoji already exist"}
      )
    )
  )
)

(defn delete-emoji [e_name]
  (sql/with-connection (db-connection)
    (sql/delete-rows :emojis ["name=?" e_name])
  )
  {:status 204}
)

(defn create-new-emoji-image [params]
  (let [nameEmoji (get params "name") emoji-without-image (assoc {} "name" nameEmoji)]
    (upload-emoji-to (get params "image") nameEmoji)
    (let [n_emoji (assoc emoji-without-image "image" 
      (str "resources/emojis/" nameEmoji "_" (get (get params "image") :filename)))]
      (println "n_msg: " n_emoji)
      (create-new-emoji n_emoji)
    )
  )
)

(defn get-image-from-emoji [emoji-name]
  (let [emoji (get (get-emoji emoji-name) :body)]
    (println "emoji get: " emoji)
    (get emoji :image)
  )
)