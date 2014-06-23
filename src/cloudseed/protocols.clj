(namespace cloudseed.protocols)

(defprotocol FSM
  "for creating event machines that accomplish things with fsm  interface"
  (run [this]
    "create and run a fsm")
