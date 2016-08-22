;;Big thanks to Richard Hull for sharing this on his excellent programming
;;enchilladas website : http://programming-enchiladas.destructuring-bind.org/

(ns bouncy-ball.ball
  (:require [ball.vector :as vec]))

(defn make-ball [x y]
  {:location (vec/make-vector x y)
   :velocity (vec/make-vector 0 1)
   :acceleration (vec/make-vector 0 1)})

(defn apply-forces [ball vec-force]
  (assoc ball :acceleration (vec/add
                            (:acceleration ball)
                            vec-force)))

(defn update-location [ball]
  (let [new-velocity (->
                      (:velocity ball)
                      (vec/add (:acceleration ball))
                      (vec/limit 5.05))
        new-location (->
                      (:location ball)
                      (vec/add new-velocity))]
    (assoc ball
           :location new-location
           :velocity new-velocity
           :acceleration (:acceleration ball))))
