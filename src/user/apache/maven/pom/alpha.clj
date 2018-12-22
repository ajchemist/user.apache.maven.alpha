(ns user.apache.maven.pom.alpha
  (:require
   [clojure.java.io :as jio]
   [clojure.string :as str]
   )
  (:import
   java.io.File
   java.io.OutputStream
   java.util.Properties
   org.apache.maven.artifact.repository.metadata.Metadata
   org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader
   org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer
   org.apache.maven.model.Build
   org.apache.maven.model.Dependency
   org.apache.maven.model.Exclusion
   org.apache.maven.model.License
   org.apache.maven.model.Model
   org.apache.maven.model.Repository
   org.apache.maven.model.Scm
   org.apache.maven.model.io.xpp3.MavenXpp3Reader
   org.apache.maven.model.io.xpp3.MavenXpp3Writer
   ))


(set! *warn-on-reflection* true)


(def ^:const MODEL_VERSION "4.0.0")


;; * model components


(defn- model-build
  ^Build
  [[path & paths]]
  (doto (Build.)
    (.setSourceDirectory path)))


(defn- model-dependency
  ^Dependency
  [[lib {:keys [mvn/version classifier exclusions] :as coord}]]
  (doto (Dependency.)
    (.setGroupId (or (namespace lib) (name lib)))
    (.setArtifactId (name lib))
    (.setVersion version)
    (.setClassifier classifier)
    (.setExclusions (map
                      (fn [lib]
                        (doto (Exclusion.)
                          (.setGroupId (or (namespace lib) (name lib)))
                          (.setArtifactId (name lib))))
                      exclusions))))


(defn- model-repository
  ^Repository
  [[^String id {:keys [^String url] :as repo}]]
  (doto (Repository.)
    (.setId id)
    (.setUrl url)))


;; * pom


(defn read-pom
  "Reads a pom file returning a maven Model object."
  ^Model
  [readable]
  (with-open [reader (jio/reader readable)]
    (.read (MavenXpp3Reader.) reader)))


(defn write-pom
  "Reads a pom file returning a maven Model object."
  [writable ^Model pom]
  (with-open [writer (jio/writer writable)]
    (.write (MavenXpp3Writer.) writer pom)))


;; ** pom read


(defn- without-nil-values
  [m]
  (into (empty m)
    (remove #(nil? (val %)))
    m))


(defn scm-to-map
  [^Scm scm]
  (without-nil-values
    {:connection           (.getConnection scm)
     :developer-connection (.getDeveloperConnection scm)
     :tag                  (.getTag scm)
     :url                  (.getUrl scm)}))


(defn license-to-map
  [^License license]
  (without-nil-values
    {:name         (.getName license)
     :url          (.getUrl license)
     :distribution (.getDistribution license)
     :comments     (.getComments license)}))


(defn model-to-map
  [^Model model]
  (without-nil-values
    {:name         (or (.getArtifactId model) (-> model .getParent .getArtifactId))
     :group        (or (.getGroupId model) (-> model .getParent .getGroupId))
     :version      (or (.getVersion model) (-> model .getParent .getVersion))
     :description  (.getDescription model)
     :homepage     (.getUrl model)
     :url          (.getUrl model)
     :licenses     (into [] (map license-to-map) (.getLicenses model))
     :scm          (when-let [scm (.getScm model)] (scm-to-map scm))
     :authors      (into [] (map (memfn ^File getName)) (.getContributors model))
     :packaging    (keyword (.getPackaging model))
     :dependencies (into []
                     (map
                       (fn [^Dependency dep]
                         {:group_name (.getGroupId dep)
                          :jar_name   (.getArtifactId dep)
                          :version    (or (.getVersion dep) "")
                          :scope      (or (.getScope dep) "compile")}))
                     (.getDependencies model))}))


;; ** pom write


(defn gen-pom
  ^Model
  [^String group-id ^String artifact-id ^String version deps paths repos]
  (doto (Model.)
    (.setModelVersion MODEL_VERSION)
    (.setGroupId group-id)
    (.setArtifactId artifact-id)
    (.setVersion version)
    (.setDependencies (map model-dependency deps))
    (.setBuild (model-build paths))
    (.setRepositories (map model-repository repos))))


;;


(defn replace-version
  ^Model
  [^Model pom version]
  (.setVersion pom version)
  pom)


(defn replace-deps
  ^Model
  [^Model pom deps]
  (.setDependencies pom (map model-dependency deps))
  pom)


(defn replace-build
  ^Model
  [^Model pom paths]
  (.setBuild pom (model-build paths))
  pom)


(defn replace-repos
  ^Model
  [^Model pom repos]
  (.setRepositories pom (map model-repository repos))
  pom)


;; * pom-properties


(defn ^Properties make-pom-properties
  [lib {:keys [:mvn/version]}]
  (let [artifact-id (name lib)
        group-id    (or (namespace lib) artifact-id)
        properties  (Properties.)]
    (.setProperty properties "groupId" group-id)
    (.setProperty properties "artifactId" artifact-id)
    (when version (.setProperty properties "version" version))
    properties))


(defn store-pom-properties
  [^OutputStream os ^Properties pom-properties ^String comments]
  (.store pom-properties os comments))


;; * finish


(set! *warn-on-reflection* false)


(comment
  (map
    model-dependency
    '{org.clojure/clojure   {:mvn/version "1.9.0" :exclusions [abc]}
      user.tools.deps.alpha {:local/root "user.tools.deps.alpha"}})
  )
