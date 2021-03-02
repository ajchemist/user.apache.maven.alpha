(ns user
  (:require
   [clojure.java.javadoc :as javadoc]
   ))


(javadoc/add-remote-javadoc "org.apache.maven.model." "https://maven.apache.org/ref/current/maven-model/apidocs/")
