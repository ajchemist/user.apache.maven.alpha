(ns user.apache.maven.pom.alpha-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [user.apache.maven.pom.alpha :refer :all]
   ))


(deftest main
  (is (= (.getModelVersion (read-pom "pom.xml")) MODEL_VERSION))
  )
