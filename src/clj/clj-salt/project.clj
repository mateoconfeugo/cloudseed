(defproject clj-salt "0.1.0"
  :description "Drop in replacement for salt using clojure to prove that the idea could be better implemented and in a more dev ops friendly way than the python salt stack way.  Ideas to be developed are using pallet as an orchastration framework and replacing yaml with edn.  Using pallet integrate this system into jenkins a common tool used among dev ops.

The project seeks to uncover dev-ops macros so that work done by dev-ops can be reproducible, testable, version controllable, reviewable, reuseable, rollbackable and automated.  Salt helps with many of these things but is made from a hodge podge of technolgies python, yaml, jinja, zeromq.  This experient will use a different hodge podge but everything will be clojure.  As this is lisp the code is data  I am hoping that this will work well with the hierarchy of states that salt brings.  I want the the system to be as easy to deploy as possible and very responsive once up.  To that end I am going to use Storm, Mesos, Marathon and Docker to accomplish the mechanics and management of the distributed system.

This system seeks to ultimately lead to a dev-ops equivalent of the big green merge button in github which will end up lifting/converging the group spec that has been assembled from the function graphs into the highstate.  Instead of merging however you are applying the highstate to the targets.  I want to use function graphs to create the highstate functions that ultimately get run.  State probably will just be macros that transform options into low state function graphs.
I am seeking to have as little program as possible and make a dsl of macros.  This way if I want a centralized approch I can run it hosted in a cider interactive session.  Highstating the system involves evaluating a function.

Some Goals:
Be super fast
Use clojure for everything so one language
Reuse as many existing projects
Adopt the nomenclature and concepts from saltstack.
Minimal code.
Create system from any laptop
Improve my clojure skills incorporating as many best practices into project as appropriate.

Technologies Used:
Pallet
Docker
Clojure
Mesos
Marathon
Storm
Jenkins
Enlive

The output of this project should be an application that can be used in conjuntion with a credit card and an account on a cloud provider to completely build an working infrastructure in the cloud along with the applications that run on this infrastructure.

I want to be able to do this in a controlled familiar manner that will provide the continous integration framework for maintaining and developing on the platform.  To do this we will use Jenkins to get us started but probably move to something a bit more stream lined.

Here is how I think it will work

* Download cloud loader life boat application from github
* Using a cloud provider run the initial application that creates a jenkins instance with a set of jobs for creating the infrastructure
* These jobs call lein pallet [group spec] up / lift / converge to create the environments, subnets, vpns, machines, services, logging, email, monitoring.
* Run these infrastructure jobs
* Install storm dependencies and create the clj salt topology across the infrastructure
* Connect into 3rd party SaaS type services
* Lanch operations server and the server that has the big green highstate button.
* Run the application deployment jobs using mesos/marathon to launch applications in docker containers across the infrastructure.
* Report on the state of the system via the clj-salt-topo


Questions:
Do we highstate from a centralized server decided upon by convention and run the pallet operations from there or do we distribute the high state function graphs to the target nodes and execute them locally.
How do the highstate function graphs get transported to the storm minion node and executed?
How to deploy a lein project into a docker container on a infrastructure application node from a jenkins job?
How to configure applictions so that they always use 127.0.0.1 and the containers via links map it to the dynamic address?

"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :aot :all
  :main clj-salt.core
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [me.raynes/fs "1.4.5"]
                 [dsabanin/clj-yaml "0.4.1"]
                 [jenkins "0.1.0-SNAPSHOT"]
                 [shoreleave "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [org.clojure/tools.cli "0.3.1"]]
  :plugins [[lein-ancient "0.5.4"]
;;            [org.thelastcitadel/jenkins-clojure-injector "0.2.1"]
            ]
  :jenkins-inject clj-salt.core/main)
