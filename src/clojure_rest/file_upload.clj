(ns clojure-rest.file_upload
      (:use ring.util.response)
      (:require 
      	[clojure.java.jdbc :as sql]
      	[clojure.contrib [duck-streams :as ds]]
      	))

(defn upload-file
  [file]
  (println "Trying to upload a file")
  (ds/copy (file :tempfile) (ds/file-str "file.out"))
  (response "File-Upload")
  )