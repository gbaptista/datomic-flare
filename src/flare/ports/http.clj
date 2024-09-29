(ns flare.ports.http
  (:require
   [flare.components.router :as router]
   [flare.controllers.meta :as controllers.meta]
   [flare.controllers.fireblade :as controllers.fireblade]))

(defn create [system]
  (router/create
   system
   [["/"                           {:get controllers.meta/handler}]

    ["/meta"                       {:get controllers.meta/handler}]

    ["/datomic/create-database"    {:post controllers.fireblade/create-database}]
    ["/datomic/delete-database"    {:delete controllers.fireblade/delete-database}]

    ["/datomic/get-database-names" {:get controllers.fireblade/get-database-names}]
    ["/datomic/list-databases"     {:get controllers.fireblade/list-databases}]

    ["/datomic/db-stats"           {:get  controllers.fireblade/db-stats
                                    :post controllers.fireblade/db-stats}]

    ["/datomic/transact"           {:post controllers.fireblade/transact}]

    ["/datomic/datoms"             {:get   controllers.fireblade/datoms
                                    :post  controllers.fireblade/datoms}]

    ["/datomic/q"                  {:get   controllers.fireblade/q
                                    :post  controllers.fireblade/q}]

    ["/datomic/entity"             {:get   controllers.fireblade/entity
                                    :post  controllers.fireblade/entity}]]))
