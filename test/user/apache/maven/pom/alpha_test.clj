(ns user.apache.maven.pom.alpha-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [clojure.java.io :as jio]
   [user.apache.maven.pom.alpha :refer :all]
   ))


(deftest main
  (is
    (= (.getModelVersion (read-pom "pom.xml")) MODEL_VERSION))


  (is
    (nil?
      (write-pom
        (jio/file (System/getProperty "java.io.tmpdir") "pom.xml")
        (gen-pom
          "user.apache.maven.alpha"
          "user.apache.maven.alpha"
          "0.0.1-SNAPSHOT"
          '{org.clojure/clojure     {:mvn/version "1.10.0"}
            user.apache.maven.alpha {:local/root "user.apache.maven.alpha"}}
          nil
          nil))))


  (is
    (==
      (count
        (get-in
          (model-to-map (read-pom (jio/file (System/getProperty "java.io.tmpdir") "pom.xml")))
          [:dependencies]))
      2))
  )


(comment
  (make-pom-properties 'user.tools.deps.alpha '{:mvn/version "0.0.1-SNAPSHOT"})
  )
