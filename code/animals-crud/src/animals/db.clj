(ns animals.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db {:classname "org.h2.Driver"
         :subprotocol "h2:file"
         :subname "/tmp/db/animals"})

(defn exists?
  "Check whether a given table exists."
  [db table]
  (try
    (do
      (->> (format "select 1 from %s" table)
           (vector)
           (jdbc/query db))
      true)
    (catch Throwable ex
      false)))
