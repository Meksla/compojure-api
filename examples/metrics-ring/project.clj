(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [metosin/compojure-api "1.1.8"]

                 [metrics-clojure "2.7.0"]
                 [metrics-clojure-ring "2.7.0"]]

  :ring {:handler example.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:plugins [[ikitommi/lein-ring "0.9.8-SNAPSHOT"]]}})
