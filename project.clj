(defproject om-root-bug "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.omcljs/om "1.0.0-alpha31-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:dev {:source-paths ["dev"]
                   :plugins [[lein-figwheel "0.5.0-1"]]}}

  :figwheel {:nrepl-port 7888}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src" ]
                        :figwheel {:on-jsload "om-root-bug.core/init"}
                        :compiler {:main "om-root-bug.core"
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :none
                                   :source-map true
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :main om-root-bug.core
                                   :optimizations :advanced
                                   :pretty-print false}}]})
