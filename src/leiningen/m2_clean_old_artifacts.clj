(ns leiningen.m2-clean-old-artifacts
  "Plugin intended for removing unused artifacts"
  (:require [leiningen.core.main :as leiningen]
            [clojure.string :as string]
            [me.raynes.fs :as fs]
            [clojure.tools.cli :as cli])
  (:import [java.io File]
           [java.nio.file Files LinkOption]
           [java.nio.file.attribute BasicFileAttributes PosixFileAttributes]
           [java.util.concurrent TimeUnit]))

(defn- get-last-acces-timestamp [^File f]
  (-> (.toPath f)
      (Files/readAttributes PosixFileAttributes (into-array LinkOption []))
      (.. lastAccessTime toMillis)))

(defn- is-old? [^long current-time ^long timeout-ms ^File f]
  (let [atime (get-last-acces-timestamp f)]
    (> (- current-time atime) timeout-ms)))

(defn- is-jar? [^File f]
  (= ".jar" (string/lower-case (fs/extension f))))

(defn- find-jar-files [mvn-directory timeout-ms]
  (let [current-time (System/currentTimeMillis)]
   (filter (every-pred fs/file? is-jar? (partial is-old? current-time timeout-ms))
           (file-seq (fs/expand-home mvn-directory)))))

(defn- days-to-ms [^long days]
  (* days 24 3600 1000))

(defn- calculate-jar-files-size-in-mb [jar-files]
  (-> (reduce (fn [total-size f]
                (+ total-size (fs/size f)))
              0 jar-files)
      (/ 1000 1000.0)
      Math/round))

(defn- clean [mvn-directory timeout-days delete?]
  (let [timeout-ms (days-to-ms timeout-days)
        jar-files (find-jar-files mvn-directory timeout-ms)
        jar-files-size (calculate-jar-files-size-in-mb jar-files)
        old-dirs (map fs/parent jar-files)]
    (leiningen/info "Found" (count jar-files) "old jar files")
    (leiningen/info "Total size is" jar-files-size "MB")
    (if delete?
      (doseq [dir old-dirs]
        (leiningen/info "Deleting" (str dir))
        (fs/delete-dir dir))
      (doseq [dir old-dirs]
        (leiningen/info (str dir))))))

(def ^:private parse-options
  [[nil "--m2-artifacts-directory=DIRECTORY" "Maven artifacts directory"
    :default "~/.m2"
    :validate [(comp fs/directory? fs/expand-home) "Must be existing directory"]]
   [nil "--days=DAYS" "Number of days witch determines artifact as old"
    :default 365
    :parse-fn #(Integer/parseInt %)
    :validate [pos? "Must be postive integer"]]
   [nil "--dry-run" "Dry run"
    :default false]])

(defn ^:no-project-needed m2-clean-old-artifacts
  "Deletes (or lists) old Maven artificats
  
  Usage: lein m2-clean-old-artifacts [--m2-artifacts-directory=DIRECTORY] [--days=DAYS] [--dry-run]

  Options:
    --m2-artifacts-directory=DIRECTORY
      Sets directory containing Maven artificats.
      Defaults to ~/.m2.

    --days=DAYS
      Sets number of days, that make artifact old. 
      Any jar file which was last accessed earlier then current time minus specified days, will be considers as old by this plugin.
      Defaults to 365.
  
    --dry-run
      Does not delete found artifacts, only lists directories that contain them.  
  "
  [project & args]
  (let [{:keys [options]} (cli/parse-opts args parse-options)]
    (clean (:m2-artifacts-directory options) (:days options) (not (:dry-run options)))))
