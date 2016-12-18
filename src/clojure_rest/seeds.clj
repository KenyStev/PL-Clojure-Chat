(ns clojure-rest.seeds
  (:require 
    [clojure-rest.emojis :refer :all]
    [clojure-rest.users :refer :all]
  )
)

; users
(def kenystev {
	:username "kenystev"
	:email "kenystev@gmail.com"
	:password "password"
	:realname "Kevin Estevez"
})
(def rnexer {
	:username "rnexer"
	:email "rnexer@gmail.com"
	:password "password"
	:realname "Nexer Rodriguez"
})
(def lisaula {
	:username "lisaula"
	:email "lisaula@gmail.com"
	:password "password"
	:realname "Luis Isaula"
})
(def raim {
	:username "raim"
	:email "raim@gmail.com"
	:password "password"
	:realname "Ricardo Interiano"
})
(def tonio {
	:username "tonio"
	:email "tonio@gmail.com"
	:password "password"
	:realname "Jose Mejia"
})
(def poio-user {
	:username "poio"
	:email "poio@chicken.pio"
	:password "password"
	:realname "Carlos Castro"
})

; emojis
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

(defn users-seed []
	(create-new-user kenystev)
	(create-new-user rnexer)
	(create-new-user lisaula)
	(create-new-user raim)
	(create-new-user tonio)
	(create-new-user poio-user)
)

(defn seed []
	(users-seed)
	(emojis-seed)
)