(ns bouncy-ball.core
  (:require [monet.canvas :as canvas]
            [dommy.core :as dommy]))

;;Create a head count which will translate to a keyword identifier
;;for each entity.
(def entity-n (atom 0))

;;Find the canvas element
(def canvas-dom (.getElementById js/document "main"))

;;create an atom for mouse position
(def mouse-position (atom {:x 0
                           :y 0}))

;;Define the canvas from canvas' getContext
(def monet-canvas (canvas/init canvas-dom "2d"))

;;This adds the square
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
  (let [the-context (:ctx monet-canvas)
        x (.-x e)
        y (.-y e)]
    (js/console.log entity-n)

    ;;Increment entity head count, for name of new entity
    (swap! entity-n inc)

    ;;Add entity to the context
    (canvas/add-entity monet-canvas (keyword (str @entity-n))
                       (canvas/entity {:x 0 :y 0 :w 500 :h 400}
                                      nil
                                      (fn [ctx val]
                                        (-> ctx
                                            (canvas/stroke-width 1)
                                            (canvas/fill-style "#ff9800")
                                            (canvas/circle {:x x
                                                            :y y
                                                            :r 15})
                                            (canvas/fill)
                                            (canvas/stroke)))))))

;;optionally add a new circle
(canvas/add-entity monet-canvas :circle
                   (canvas/entity {:x 0 :y 0 :w 500 :h 400}
                                  nil
                                  (fn [ctx val]
                                    (-> ctx
                                        (canvas/stroke-width 1)
                                        (canvas/fill-style "#e91e63")
                                        (canvas/circle {:x (:x mouse-position)
                                                        :y (:y mouse-position)
                                                        :r 15})
                                        (canvas/fill)
                                        (canvas/stroke)))))


;;create an event listener for the mouse position.
(dommy/listen! canvas-dom :mousemove move-handler)

;;create an event listener for the click
(dommy/listen! canvas-dom :click click-handler)
