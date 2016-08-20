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

(defn handle-incoming-msg [event world-state]
  (->
    world-state
    (merge event)))

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
  (swap! entity-n inc)

  (let [new-ball (ball/make-ball (nth (client-coords event) 0)
                                 (nth (client-coords event) 1))
        new-name (keyword (str "ball-" @entity-n))]
    (assoc-in world-state [:entities new-name] new-ball)))

(defn draw-all-balls [world-state ctx]
  (doall (map (fn [x]
                (draw-ball ctx (:location (nth x 1)) 15 "#2196f3"))
              (:entities world-state))))

(defn update-ent [event {:keys [mouse-coords entities] :as world-state}]
  (loop [in     entities
         out    []]
    (if (empty? in)
      (assoc world-state :entities out)
      (let [shape  (->
                     (first in)
                     (update-in [:entities] ball/update-location in))]
        (recur
          (next in)
          (conj out shape))))))

(defn render [world-state]
  ;;For some reason I have to update the atom in render.
  (clear whole-ctx)
  (draw-all-balls world-state whole-ctx)
  (draw-floor whole-ctx 500 "#2196f3")
  (draw-ball whole-ctx (:mouse-coords world-state) 15 "#e91e63"))

(go
  (big-bang
    :initial-state initial-state
    :on-mousemove update-state
    :on-tick update-ent
    :on-click add-ball
    :to-draw render))
