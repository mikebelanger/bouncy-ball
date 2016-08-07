(ns bouncy-ball.core
  (:require [monet.canvas :as canvas]
            [dommy.core :as dommy]))

;;Create a head count which will be translated to a keyword identifier
;;for each entity.
(def entity-n (atom 0))

;;Find the canvas element
(def canvas-dom (.getElementById js/document "main"))

;;create an atom for mouse position
(def mouse-position (atom {:x 0
                           :y 0}))

;;Define the canvas from canvas' getContext
(def monet-canvas (canvas/init canvas-dom "2d"))

(defn change-xy [mc k x y]
  "Changes the entity k's :value-> :x/:y values to the x y parameters"
  (canvas/update-entity mc k (fn [e] (assoc-in e [:value :x] x)))
  (canvas/update-entity mc k (fn [e] (assoc-in e [:value :y] y))))

(defn add-behavior [mc k func]
  "Gives the entity a new update behavior"
  (canvas/update-entity mc k (fn [e] (assoc e :update func))))

(defn add-force [val k]
  "To be attached to an entity's :update key, val represents entities' :value
  and k the key (usually x or y values)"
  (assoc val k (+ 1 (k val))))

;;This adds the background
(canvas/add-entity monet-canvas :blue-background
                   (canvas/entity {:x 0 :y 0 :w 500 :h 400} ; val
                                  nil                       ; update function
                                  (fn [ctx val]             ; draw function
                                    (-> ctx
                                        (canvas/fill-style "#3a638c")
                                        (canvas/fill-rect val)))))


;;Get current mouse location
(defn move-handler [e]
    (set! mouse-position {:x (.-x e)
                          :y (.-y e)}))

;;handle the mouse clicks
(defn click-handler [e]
  "Wherever you click, a ball appears.  First let's grab the mouse coordinates"
  (let [the-context (:ctx monet-canvas)
        x (.-x e)
        y (.-y e)]

    ;;Increment entity head count, for name of new entity
    (swap! entity-n inc)

    ;;Get the new name
    (let [new-name (keyword (str "circle-" @entity-n))]
    ;;Add entity to the context
      (canvas/add-entity monet-canvas new-name
                         (canvas/entity {:x x :y y :w 15 :h 15}
                                        nil
                                        (fn [ctx val]
                                          (-> ctx
                                              (canvas/fill-style "#ff5722")
                                              (canvas/circle {:x (:x val)
                                                              :y (:y val)
                                                              :r 15})
                                              (canvas/fill)))))
  ;;Now we'll add gravity
  (add-behavior monet-canvas new-name (fn [val] (assoc val :y (+ 1 (:y val))))))))

;;This draws the 'preview' circle by following the cursor.
(canvas/add-entity monet-canvas :preview-circle
                   (canvas/entity {:x (:x mouse-position)
                                   :y (:y mouse-position)
                                   :w 15
                                   :h 15}
                                  nil
                                  (fn [ctx val]
                                    (-> ctx
                                        (canvas/fill-style "#e91e63")
                                        (canvas/circle {:x (:x mouse-position)
                                                        :y (:y mouse-position)
                                                        :r 15})
                                        (canvas/fill)))))


;;create an event listener for the mouse position.
(dommy/listen! canvas-dom :mousemove move-handler)

;;create an event listener for the click
(dommy/listen! canvas-dom :click click-handler)
