(ns om-root-bug.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state (atom {:data {:id "1" :name "Ted" :type "cool-stuff"}}))

(defui ^:once ChildComponent
  static om/IQuery
  (query [this]
         [:id :name :type])
  Object
  (render [this]
          (let [{:keys [id name type]} (-> this om/props)]
            (dom/div nil
                     (dom/h1 nil id)
                     (dom/h1 nil name)
                     (dom/h1 nil type)))))

(def child-component (om/factory ChildComponent))

(defui ^:once RootComponent
  static om/IQuery
  (query [this]
         [{:data (om/get-query ChildComponent)}])
  Object
  (render [this]
          (child-component (-> this om/props :data))))

(defn read
  [{:keys [state query parser] :as env} key _]
  (let [st @state]
    {:value (om/db->tree query (key st) st)}))

(defn mutate
  [{:keys [state query parser] :as env} key _]
  {:action #(swap! state update-in [:data :id] (constantly "2"))})

(def parser (om/parser {:read read :mutate mutate}))

(defonce reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(defonce root (atom nil))

(defn ^:export init
  []
  (if (nil? @root)
    (let [target (js/document.getElementById "app")]
      (om/add-root! reconciler RootComponent target)
      (reset! root RootComponent))
    (om/force-root-render! reconciler)))

(when-not @root
  (init))

(prn "class-path->query "(-> reconciler om/get-indexer deref :class-path->query))

(js/setTimeout #(do (om/transact! reconciler `[(new/id) :data])
                    (om/set-query! reconciler {:query [{:data [:id]}]})
                    (prn "class-path->query" (-> reconciler om/get-indexer deref :class-path->query)))
               1000)
