(ns clojure-rest.file_upload
      (:use ring.util.response)
      (:require 
      	[clojure.java.jdbc :as sql]
        [clojure.java.io :refer :all]
      	[clojure.contrib [duck-streams :as ds]]
      	))

(defn upload-file
  [file namefile]
  (println "Trying to upload a file")
  (ds/copy (file :tempfile) (ds/file-str namefile))
  (response "File-Upload")
  )

(defn upload-file-to
  [file namefile]
  (println "Trying to upload a file")
  (let [dir-name (str "db/profilePictures/" namefile)]
    (make-parents dir-name)
    (ds/copy (file :tempfile) (ds/file-str dir-name))
    (response "File-Upload")
    )
  )

(defn upload-image-to
  [file namefile]
  (println "Trying to upload a image")
  (let [dir-name (str "db/images/" namefile "_" (get file :filename))]
    (make-parents dir-name)
    (ds/copy (file :tempfile) (ds/file-str dir-name))
    (response "File-Upload")
    )
  )

(defn upload-emoji-to
  [file namefile]
  (println "Trying to upload a image")
  (let [dir-name (str "resources/emojis/" namefile "_" (get file :filename))]
    (make-parents dir-name)
    (ds/copy (file :tempfile) (ds/file-str dir-name))
    (response "File-Upload")
    )
  )