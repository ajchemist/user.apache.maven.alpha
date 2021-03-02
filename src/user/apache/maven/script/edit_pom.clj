(ns user.apache.maven.script.edit-pom
  (:require
   [clojure.java.io :as jio]
   [clojure.tools.cli :as cli]
   [user.apache.maven.pom.alpha :as pom]
   ))


(def cli-options
  [["-p" "--pom POM_FILE" "pom file location"
    :default "pom.xml"]
   [nil "--scm-url SCM_URL" "SCM url"]])


(defn -main
  [& xs]
  (let [{{:keys [pom
                 scm-url]} :options
         :as               _parsed-opts} (cli/parse-opts xs cli-options)
        model                            (pom/read-pom pom)]
    (pom/replace-scm model {:url scm-url})
    (pom/write-pom (jio/file pom) model)))
