(ns animals.animals
  (:refer-clojure :exclude [read])
  (:require [clojure.java.jdbc :as jdbc]
            [animals.db :as db]
            [schema.core :as s]))

(def Animal
 {:name s/Str
  :species s/Str})

(defn create!
  ([db m]
   (s/validate Animal m)
   (let [result (jdbc/insert! db :animals m)
         id (get (first result) (keyword "scope_identity()"))]
     id)))

(defn read
  ([db]
   (jdbc/query db ["select * from animals"]))
  ([db id]
   (first (jdbc/query db [(str "select * from animals\n"
                               "where id = ?") id]))))

(defn update!
  [db id m]
  (jdbc/update! db :animals m ["id = ?" id]))

(defn delete!
  ([db]
   (jdbc/execute! db ["delete from animals"]))
  ([db id]
   (jdbc/delete! db :animals ["id = ?" id])))

(defn insert-samples! [db]
  (println "inserting some animals")
  (do
    (create! db {:name    "Painted-snipe"
                 :species "Rostratulidae"})

    (create! db {:name    "Yellow-backed duiker"
                 :species "Cephalophus silvicultor"})
    (create! db {:name    "Aardwolf"
                 :species "Proteles cristata"})
    (create! db {:name    "Gnu"
                 :species "Dbochaetes gnou"})
    (create! db {:name    "Curled octopus"
                 :species "Eledone cirrhosa"})
    (create! db {:name    "Horny toad"
                 :species "Phrynosoma cornutum"})
    (create! db {:name    "Dung beetle"
                 :species "Scarabaeus sacer"})
    (create! db {:name    "Atlantic salmon"
                 :species "Salmo salar"})))

(defn init
  [db]
  (jdbc/with-db-transaction [conn db]
    (if-not (db/exists? conn "animals")
      (do
        (println "creating animals table")
        (jdbc/execute! conn
                       [(jdbc/create-table-ddl :animals
                                               [:id "bigint primary key auto_increment"]
                                               [:name "varchar"]
                                               [:species "varchar"])])
        (insert-samples! conn))
      (println "table animals already exists"))))
