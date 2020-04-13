(ns user.apache.maven.script.get-pom-version
  (:require
   [clojure.tools.cli :as cli]
   [user.apache.maven.pom.alpha :as pom]
   ))


(def cli-options
  [["-p" "--pom POM_FILE" "pom file location"
    :default "pom.xml"]])


(defn -main
  [& xs]
  (let [{:keys [pom] :as parsed-opts} (cli/parse-opts xs cli-options)]
    (println (.getVersion (pom/read-pom "pom.xml"))))  )
