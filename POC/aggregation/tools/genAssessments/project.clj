(defproject genAssessments "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :main genAssessments.core
  :run-aliases { :extra-large [genAssessments.core -main ""]}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/data.xml "0.0.6"]
                 [org.clojure/data.json "0.1.3"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [com.novemberain/monger "1.2.0"]]
  :jvm-opts ["-Xms256m", "-Xmx4096m"]
  :shell-wrapper true)
