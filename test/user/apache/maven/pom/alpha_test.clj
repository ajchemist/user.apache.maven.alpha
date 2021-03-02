(ns user.apache.maven.pom.alpha-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [clojure.java.io :as jio]
   [user.apache.maven.pom.alpha :as pom]
   ))


(deftest main
  (is
    (= (.getModelVersion (pom/read-pom "pom.xml")) pom/MODEL_VERSION))


  (is
    (nil?
      (pom/write-pom
        (jio/file (System/getProperty "java.io.tmpdir") "pom.xml")
        (pom/gen-pom
          "user.apache.maven.alpha"
          "user.apache.maven.alpha"
          "0.0.1-SNAPSHOT"
          '{org.clojure/clojure     {:mvn/version "1.10.0"}
            user.apache.maven.alpha {:local/root "user.apache.maven.alpha"}}
          nil
          nil
          nil))))


  (is
    (==
      (count
        (get-in
          (pom/model-to-map (pom/read-pom (jio/file (System/getProperty "java.io.tmpdir") "pom.xml")))
          [:dependencies]))
      2))
  )


(comment
  (pom/make-pom-properties 'user.tools.deps.alpha '{:mvn/version "0.0.1-SNAPSHOT"})
  )
