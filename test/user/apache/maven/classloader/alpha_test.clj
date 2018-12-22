(ns user.apache.maven.classloader.alpha-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [user.apache.maven.classloader.alpha :refer :all]
   ))


(deftest main
  (is
    (=
      (clojure-version)
      (get-version 'org.clojure/clojure)
      (get-version-from 'org.clojure/clojure "pom.xml")
      (get-version-from 'org.clojure/clojure "pom.properties")))
  )
