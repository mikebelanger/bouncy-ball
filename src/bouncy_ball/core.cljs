(ns bouncy-ball.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo]
            [dommy.core :as dommy]
            [cljs.core.async :as async :refer [<!]]
            [big-bang.core :refer [big-bang]]
            [big-bang.events.browser :refer [client-coords]]
            [bouncy-ball.ball :as ball]))

;;Randomly generate colors
(def colors [
  "#E16889"
  "#FE853E"
  "#6EC59B"
  "#FDBA52"
  "#F5DED0"
  "#94614C"
  "#2D97D3"
  "#48C3CB"
  "#A9A6D3"
  "#C0C1BC"
])

;;Create a head count which will be translated to a keyword identifier
;;for each entity.
(def entity-n (atom 0))

;;Find the canvas element
(def canvas-dom (.getElementById js/document "main"))

;;Define the canvas from canvas' getContext
(def whole-ctx (canvas/get-context canvas-dom :2d))

(def initial-state {
  :mouse-coords [0 0]
  :entities {}
})

(defn clear [ctx]
  (canvas/clear-rect ctx {:x 0
                          :y 0
                          :w 500
                          :h 500}))

;;Draw ball on ball-canvas node for a 'preview' node.
(defn draw-ball [ctx location radius col]
  (canvas/fill-style ctx col)
  (canvas/circle ctx  {:x (nth location 0)
                       :y (nth location 1)
                       :r radius})
  (canvas/fill ctx))

(defn draw-floor [ctx w col]
  (canvas/fill-style ctx col)
  (canvas/fill-rect ctx {:x 0 :y 400 :w w :h 20}))

(defn update-state [event world-state]
  (assoc world-state :mouse-coords (vector (.-x event) (.-y event))))

(defn add-ball [event world-state]
  (js/console.log world-state "add-ball count: " (count (:entities world-state)))
  (swap! entity-n inc)

  (let [new-ball (ball/make-ball (nth (client-coords event) 0)
                                 (nth (client-coords event) 1))
        updated-entities (merge (:entities world-state) new-ball)]

        (assoc world-state :entities updated-entities)))

(defn update-ent [event world-state]
  (let [new-entities (->> (:entities world-state)
                          (map #(ball/apply-forces % [0 10]))
                          (map #(ball/update-location %)))]
    (assoc world-state :entities new-entities)))

(defn render [{:keys [entities]} world-state]
  (clear whole-ctx)
  (doseq [entity entities]
    (draw-ball whole-ctx (:location entity) 15 "#8bc34a"))

  (draw-floor whole-ctx 500 "#2196f3"))

(go
  (big-bang
    :initial-state initial-state
    :tick-rate 30
    :on-tick update-ent
    :on-mousemove update-state
    :on-click add-ball
    :to-draw render))
