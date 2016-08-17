(ns bouncy-ball.core
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo]
            [dommy.core :as dommy]
            [big-bang.core :refer [big-bang]]
            [big-bang.events.browser :refer [client-coords]]))

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

(def width 500)
(def height 400)
(def mouse-coords (atom [50 50]))

;;Create a head count which will be translated to a keyword identifier
;;for each entity.
(def entity-n (atom 0))

;;Find the canvas element
(def canvas-dom (.getElementById js/document "main"))

;;Define the canvas from canvas' getContext
(def whole-ctx (canvas/get-context canvas-dom :2d))

;;Draw ball on ball-canvas node for a 'preview' node.

(defn draw-ball [ctx radius]
  (canvas/clear-rect whole-ctx {:x 0
                                :y 0
                                :w 500
                                :h 500})
  (canvas/fill-style ctx "#e91e63")
  (canvas/circle ctx  {:x (nth mouse-coords 0)
                       :y (nth mouse-coords 1)
                       :r radius})
  (canvas/fill ctx))

(defn draw-floor []
  (let [val {:x 0 :y 480 :w 500 :h 20}] ; val
        (-> whole-ctx
            (canvas/fill-style "#3a638c")
            (canvas/fill-rect val)
            (canvas/fill))))

(defn update-state [event world-state]
  (assoc world-state :mouse-coords (client-coords event)))

(defn render [world-state]
  (set! mouse-coords (:mouse-coords world-state))
  (draw-floor)
  (draw-ball whole-ctx 15)
  (js/console.log mouse-coords))

(big-bang
  :initial-state {:mouse-coords [0 0]}
  :on-mousemove update-state
  :to-draw render)
