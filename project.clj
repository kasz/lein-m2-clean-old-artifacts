(defproject lein-m2-clean-old-artifacts "0.1.0-SNAPSHOT"
  :description "Leiningen plugin intended for removing unused artifacts"
  :url "https://github.com/kasz/lein-m2-clean-old-artifacts"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/tools.cli "0.3.5"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true)
