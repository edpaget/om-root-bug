(ns om-root-bug.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state (atom {:data {:id "1" :name "Ted" :type "cool-stuff"}}))

(defui ^:once RootComponent
  static om/IQuery
  (query [this]
         [{:data [:id :name :type]}])
  Object
  (render [this]
          (prn (om/props this))
          (let [{:keys [id name type]} (-> this om/props :data)]
            (dom/div nil
                     (dom/h1 nil id)
                     (dom/h1 nil name)
                     (dom/h1 nil type)))))

(defn read
  [{:keys [state query parser] :as env} key _]
  (let [st @state]
    {:value (om/db->tree query (key st) st)}))

(def parser (om/parser {:read read :mutate (fn [])}))

(defonce reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(defonce root (atom nil))

(defn ^:export init
  []
  (prn "HERE")
  (if (nil? @root)
    (let [target (js/document.getElementById "app")]
      (om/add-root! reconciler RootComponent target)
      (reset! root RootComponent))
    (om/force-root-render! reconciler)))

(init)

(prn (-> reconciler om/get-indexer deref :class-path->query))

(js/setTimeout #(do (om/set-query! reconciler {:query [{:data [:id]}]})
                    (prn (-> reconciler om/get-indexer deref :class-path->query)))
               5000)
