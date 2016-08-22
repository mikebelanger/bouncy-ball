;;Big thanks to Richard Hull for sharing this on his excellent programming
;;enchilladas website : http://programming-enchiladas.destructuring-bind.org/

(ns bouncy-ball.ball
  (:require [bouncy-ball.vector :as vec]))

(defn make-ball [x y color]
  {:location (vec/make-vector x y)
   :velocity (vec/make-vector 0 0.5)
   :acceleration (vec/make-vector 0 0.5)
   :radius 15
   :color color})

(defn apply-forces [ball vec-force acc]
  "Acc is just for some kind of multiplying effect needed for acceleration."
  (assoc ball :acceleration (-> (:acceleration ball)
                                (vec/mult acc)
                                (vec/add vec-force))))

(defn ball-surface-collision? [ball-loc span-loc axis rad]
  "Checks for collisions between ball and flat surface spanning either x or y."
  (let [col-vector    (if (= :y axis)
                            (vec/make-vector (first ball-loc) (last span-loc))
                            (vec/make-vector (first span-loc) (last ball-loc)))

        space-between (vec/dist ball-loc col-vector)
        collided      (<= space-between rad)]

    collided))

(defn outside-bounds [ball bounds]
  "Returns the vector that's either at it's limit, or the original if its in bounds."
  (let [new-val (vec/make-vector (min (first ball) (first bounds))
                                 (min (last  ball) (last bounds)))]
        new-val))

(defn bounce [ball span-loc axis rad]
  "Checks if there's any collisions, and inverts acceleration if there is."
  (if (ball-surface-collision? (:location ball) span-loc axis rad)

      (assoc ball :velocity (vec/mult (:velocity ball) -0.97)
                  :location (outside-bounds (:location ball) span-loc))

      (assoc ball :acceleration (:acceleration ball))))

(defn update-location [ball]
  (let [new-velocity (->
                      (:velocity ball)
                      (vec/add (:acceleration ball)))
        new-location (->
                      (:location ball)
                      (vec/add new-velocity))]
    (assoc ball
           :location new-location
           :velocity new-velocity
           :acceleration (:acceleration ball))))
