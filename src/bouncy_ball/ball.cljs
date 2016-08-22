;;Big thanks to Richard Hull for sharing this on his excellent programming
;;enchilladas website : http://programming-enchiladas.destructuring-bind.org/

(ns bouncy-ball.ball
  (:require [bouncy-ball.vector :as vec]))

(defn make-ball [x y]
  {:location (vec/make-vector x y)
   :velocity (vec/make-vector 0 1)
   :acceleration (vec/make-vector 0 1)
   :radius 15})

(defn apply-forces [ball vec-force]
  (assoc ball :acceleration (vec/add
                            (:acceleration ball)
                            vec-force)))

(defn ball-surface-collision? [ball-loc span-loc axis rad]
  "Checks for collisions between ball and flat surface spanning either x or y."
  (let [col-vector    (if (= :y axis)
                            (vec/make-vector (first ball-loc) (last span-loc))
                            (vec/make-vector (first span-loc) (last ball-loc)))

        space-between (vec/dist ball-loc col-vector)
        collided      (<= space-between rad)]

    collided))

(defn bounce [ball span-loc axis rad]
  "Checks if there's any collisions, and inverts acceleration if there is."
  (if (ball-surface-collision? (:location ball) span-loc axis rad)
      (assoc ball
             :acceleration (vec/mult (:acceleration ball) -1))
      (assoc ball :acceleration (:acceleration ball))))

; (defn set-boundries [ball min-x max-x min-y max-y]
;   "Checks if an object is outside of the bounds x and y, and if so, ")
;   (if (<= (:location ball) span-loc axis rad)
;       (assoc ball
;              :location (vec/mult (:acceleration ball) -1))
;       (assoc ball :acceleration (:acceleration ball)))

(defn update-location [ball]
  (let [new-velocity (->
                      (:velocity ball)
                      (vec/add (:acceleration ball))
                      (vec/limit 10.05))
        new-location (->
                      (:location ball)
                      (vec/add new-velocity))]
    (assoc ball
           :location new-location
           :velocity new-velocity
           :acceleration (:acceleration ball))))
