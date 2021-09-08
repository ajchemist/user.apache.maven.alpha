(ns user.apache.maven.api
  (:require
   [clojure.java.io :as jio]
   [user.apache.maven.pom.alpha :as pom]
   ))


(defn print-pom-version
  [{:keys [pom-file]
    :or   {pom-file "pom.xml"}}]
  (assert (.exists (jio/file pom-file)))
  (println (.getVersion (pom/read-pom pom-file))))
