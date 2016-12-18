(ns clojure-rest.seeds
  (:require 
    [clojure-rest.emojis :refer :all]
  )
)

(def poio {:name "poio" :image "resources/emojis/poio.png"})
(def laugh {:name "laugh" :image "resources/emojis/laugh.png"})
(def shit {:name "shit" :image "resources/emojis/shit.png"})
(def pizza {:name "pizza" :image "resources/emojis/pizza.png"})
(def cien {:name "100" :image "resources/emojis/100.png"})
(def heart {:name "heart" :image "resources/emojis/heart.png"})


(defn emojis-seed []
	(create-new-emoji poio)
	(create-new-emoji laugh)
	(create-new-emoji shit)
	(create-new-emoji pizza)
	(create-new-emoji cien)
	(create-new-emoji heart)
)

(defn seed []
	(emojis-seed)
)