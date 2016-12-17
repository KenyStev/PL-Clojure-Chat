(ns clojure-rest.db_setup
	(:use clojure.java.io)
	(:require 
      	[clojure-rest.db_config :refer :all]
      	[clojure.java.jdbc :as sql]
    )
)

(if (not (.exists (as-file "db/clojurechat.h2.db") ))
  (sql/with-connection (db-connection)
    ;(sql/drop-table :users) ; no need to do that for in-memory databases
    (sql/create-table :users
      [:email "varchar(150)" "primary key"]
      [:username :varchar]
      [:password :varchar]
      [:realname :varchar]
      [:profile_picture :varchar]
    )

    (sql/create-table :friends 
      [:id "varchar(256)" "primary key"]
      [:user_id1 "varchar(256)"]
      [:user_id2 "varchar(256)"]
      [:since "datetime"]
      ["foreign key" "(user_id1) references users(email)"]
      ["foreign key" "(user_id2) references users(email)"]
    )

    (sql/create-table :emojis 
      [:id "varchar(256)" "primary key"]
      [:name "varchar(256)"]
      [:image "varchar(1024)"]
    )

    (sql/create-table :messages 
      [:id "varchar(256)" "primary key"]
      [:from_who "varchar(256)"]
      [:to_who "varchar(256)"]
      [:message "varchar(2048)"]
      [:sent "datetime"]
      [:seen "boolean"]
      [:type "varchar(4)"]
    )

    (sql/create-table :rooms
      [:name "varchar(256)" "primary key"]
      [:created_at "datetime"]
    )

    (sql/create-table :rooms_users
      [:id "varchar(256)" "primary key"]
      [:room_id "varchar(256)"]
      [:user_id "varchar(256)"]
      [:is_admin "boolean"]
      ["foreign key" "(room_id) references rooms(name)"]
      ["foreign key" "(user_id) references users(email)"]
    )

    ;(sql/transaction
    ;  ["create table FRIENDS ( id varchar(256), user_id_1 varchar(256), user_id2 varchar(256),
    ;    since date, primary key (id), foreign key (user_id1) references users(id),
    ;    foreign key (user_id2) references users(id)
    ;  )"]
    ;)
  )
)