(ns clojure-rest.db_config
	(:import com.mchange.v2.c3p0.ComboPooledDataSource)
	)

	(def db-config
	      {:classname "org.h2.Driver"
	       :subprotocol "h2"
	       :subname "db/clojurechat"
	       :user ""
	       :password ""})

    (defn pool
      [config]
      (let [cpds (doto (ComboPooledDataSource.)
                   (.setDriverClass (:classname config))
                   (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
                   (.setUser (:user config))
                   (.setPassword (:password config))
                   (.setMaxPoolSize 6)
                   (.setMinPoolSize 1)
                   (.setInitialPoolSize 1))]
        {:datasource cpds}))

    (def pooled-db (delay (pool db-config)))

    (defn db-connection [] @pooled-db)

    (defn uuid [] (str (java.util.UUID/randomUUID)))