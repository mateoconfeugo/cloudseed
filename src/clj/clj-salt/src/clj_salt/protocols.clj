(ns clj-salt.protocols)

(defprotocol FSM
  "for creating event machines that accomplish things with fsm  interface"
  (run [this graph]
    "create and run a fsm"))
