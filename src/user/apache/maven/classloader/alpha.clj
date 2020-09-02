(ns user.apache.maven.classloader.alpha
  (:require
   [clojure.java.io :as jio]
   [user.apache.maven.pom.alpha :as pom]
   ))


(set! *warn-on-reflection* true)


(defn meta-inf-resource
  ^java.net.URL
  [lib component]
  (let [group-id    (or (namespace lib) (name lib))
        artifact-id (name lib)
        path        (str "META-INF/maven/" group-id "/" artifact-id "/" component)]
    (jio/resource path)))


(defmulti get-version-from {:arglists '([lib from])} (fn [_lib from] from))


(defmethod get-version-from "pom.properties"
  [lib _from]
  (when-let [r (meta-inf-resource lib "pom.properties")]
    (with-open [stream (jio/input-stream r)]
      (get (doto (java.util.Properties.) (.load stream)) "version"))))


(defmethod get-version-from "pom.xml"
  [lib _from]
  (when-let [r (meta-inf-resource lib "pom.xml")]
    (.getVersion (pom/read-pom r))))


(defn get-version
  [lib]
  (or
    (get-version-from lib "pom.properties")
    (get-version-from lib "pom.xml")
    nil))


(set! *warn-on-reflection* false)
