(defproject marauder-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :ring {:handler marauder-api.handler}
  :main marauder-api.core
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring "1.4.0"]
                 [compojure "1.4.0" :exclusions [joda-time]]
                 [ring/ring-json "0.4.0" :exclusions [joda-time]]
                 [http-kit "2.1.19"]])
