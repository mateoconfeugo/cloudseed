(ns pallet.groups.datomic )

(group-spec "node-with-datomic"
   :extends [(pallet.crate.datomic/server-spec {})])
