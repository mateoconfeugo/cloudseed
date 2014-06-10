(defproject cloudseed  "0.1.0"
  :description "A comparision agaisnt salt using clojure to show that the idea could be better implemented and in a more dev ops friendly way than the python salt stack way.  Ideas to be developed are using pallet as an orchastration framework and replacing yaml with edn.  Using pallet integrate this system into jenkins a common tool used among dev ops.

The project seeks to uncover dev-ops macros so that work done by dev-ops can be reproducible, testable, version controllable, reviewable, reuseable, rollbackable and automated.  Salt helps with many of these things but is made from a hodge podge of technolgies python, yaml, jinja, zeromq.  This experient will use a different hodge podge but everything will be clojure.  As this is lisp the code is data  I am hoping that this will work well with the hierarchy of states that salt brings.  I want the the system to be as easy to deploy as possible and very responsive once up.  To that end I am going to use Storm, Mesos, Marathon and Docker to accomplish the mechanics and management of the distributed system.

This system seeks to ultimately lead to a dev-ops equivalent of the big green merge button in github which will end up lifting/converging the group spec that has been assembled from the function graphs into the highstate.  Instead of merging however you are applying the highstate to the targets.  I want to use function graphs to create the highstate functions that ultimately get run.  State probably will just be macros that transform options into low state function graphs.
I am seeking to have as little program as possible and make a dsl of macros.  This way if I want a centralized approch I can run it hosted in a cider interactive session.  Highstating the system involves evaluating a function.

Some Goals:
The infrastructure actually is a coherent distributed program we develop against.
Be super fast
Use clojure for everything so one language
Reuse as many existing projects
Adopt the nomenclature and concepts from saltstack.
Minimal code.
Create system from any laptop
Improve my clojure skills incorporating as many best practices into project as appropriate.
Aggregate the various web tools with a top page that has links to them.
Make our system cloud provider independent
Eliminate all magic IPs, ports, hostnames networks and other such stuff that needs to be configurable at deployment time

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

The hope is that in the end there is very little code to describe a businesses dev-ops system.

Here is how I think it will work.

Consider the following scenario:

Lets say the worse thing happens and some one completely destroys every box in your cloud effectively ending you online presence.

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
Is this project way to ambitious to accomplish something that already exists?
Do we highstate from a centralized server decided upon by convention and run the pallet operations from there or do we distribute the high state function graphs to the target nodes and execute them locally.
How do the highstate function graphs get transported to the storm minion node and executed?
How to deploy a lein project into a docker container on a infrastructure application node from a jenkins job?
How to configure applictions so that they always use 127.0.0.1 and the containers via links map it to the dynamic address?
What crates are available to help me create an identical system?
What are the top level crates that mimic the state modules behaviour in saltstack?

Why would this be cooler?

One language and a very powerful one at that and the artifact that the dev-ops team works on is a real living program.
How saltstack is made now is like the clunky set of technogies the web is made out of maybe its necessary for the web to be like that
but I don't see how a dev-ops framework, implentation philosphy has to be.  I think an honest person would find it hard to argue that python is a more powerful language than clojure.  Maybe easier to learn and therefore powerful in that mere mortals you can find and hire is more useful than the incredible productivity gains realized in a correctly run and architected clojure system. I believe a well made python system will be much larger,  harder to maintain more difficult to deploy and extend then a compreble clojure project

Salt stack is made out of mediocre technology not bad but not super cool either and like the web very different lets take a look
Python - Javascript  lots of idocynrcias because of making it easy
YAML - HTML  so yaml is a markup language which like HTML is okay but could be way simpler and better
CSS - Jinja okay CSS is way better than jinja its difficult know where to begin on why jinja sucks

So salt can actually have python renders rather than jinja but the docs all sugguest otherwise this seems stupid.  People using this type of technology are by their very nature trying simplify, organize, DRY things up.  Encouraging them to use a stupid templating system so as to control their behaviour seems short sited instead focus on how the system should be used and then use just python. Don't use YAML use some sort of convention for using python itself as a configuration data language.

This artifical encouraged impediment once removed would eliminate my approachs advantage of using a single programming language for all aspects of the dev-ops engineers day.
This way instead of learning how to work around all the limitations of jinja and YAML they get better at using python.  You can then even use the python debugger as a central ops master like you could a clojure REPL.  Oddly this isn't sugguested or encouraged anywhere.  One could argue because of pythons shackle imposing one way of doing things approach to development this might be preferable to clojure in an environment where standardization quick understanding by maintainers is valued more than semantic, syntactic, and performance benefits I would argue a clojure approach would have over python.  I will concede that for right now its probably easier to find good python programmers in your area than clojure style.

I think however ultimately the argument that makes the clojure approach to dev-ops better than the pythonic one is that clojure seems to expands ones ability and multipy their efficacy much more than python.  Clojure allows one a clear way to reason about ones programs and since this approach turns the sysmtem in to a genuie program it should make the system easir to reason about.  That is the hope.

I hope I am not misunderstood on the subjects of saltstack and python.  I think both are great although I've never really gotten the all the python excitement.  I particular think salt is wonderful and such a huge advance in the right direction.

but lets be frank compared to clojure python and all the pythonic best practices and idioms do noting to improve able to mentally reason and think about the structure and behavious os the computational process I am engaging to cause the desired system behaviour.  Thats the beauty of lisp not only does it help build better programs with less code it lets you think about them in a more straightword and ultimately easy manner.

Python linguistic feature just doesn't do this.  Also because of its homoiconic nature clojue supports macros which allow the developer to create semantic constructs as part of the very language with which to then construct the desired domain specific bahaviour from.  This two step process of 1) creating a DSL 2) using the DSL to create the desired application is much easy and faster in clojure than in python which leads to much less code to maintain, manage and debug.

Another advange clojure has over python is that clojure in its native way of being is compiled down in to java byte code using the JIT and then run.  Python is an interpretted language it can't do that you can compile into pyc files and thats good but not dynamically I believe and not with the feature set the JVM provides and application over its lifecycle.

Enough of the rabble the proof is in the tasting and that means if it is almost trival to create this system in clojure using the frameworks and technolgies previously discussed it will go to illustrate that onen is getting closer to the most elegant solution for how to do dev-ops work.



So this will probably come down to what pallet crates I can find on the internet


Dev-ops needs to interface technicalogicl automation with humans and their managerial process.  This seems to best be modeled as an asynchronous workflow that carries forward state.  This what jenkins is. Jenkins along with its many plugins is itself an operating system.  Jenkins utility is that it allows a level granularity that is well suited for people while allowing things to be scheduled or event driven.  Also those currently involved in dev ops are usually aquainted with Jenkins.  My hope is that when one uses this cloudseeding application to build out their infrastructure they will be easily to reconstruct how it was done in the logical chunks via the jenkins jobs and organized via the views.  This way any notion of magic is removed from how your system got from state A to state B from an infrastructure standpoint.

Some things that makes dev-ops engineer mad are
* Difficultly creating an environment to test in let along capture as some sore of programmatic test
* The cycle time between making a system level change and seeing if that is reflecting correctly in the system state.
* Time and resources spinning up a test environment
* Explaining how that works and is different to developers who right what to use this system that simulates the application in the system context they are working on.
* Affecting the work the a tasks tick describes in a manner that translates into measurable piece of work
* How to make a piece of work atomic to use in the release process as well as in the operations aspect of the system how to cherry pick this into a different branch or more importantly how to roll it back
* We want a business component factory and what we have more resembles the DMV

Steps

Show that this works using the virtual box, rackspace and amazone  providers for our applications.

"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :aot :all
;;  :main cloudseed.core
  :ring {:handler cloudseed.handler/war-handler :auto-reload? true :auto-refresh true}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["private" {:url "s3p://marketwithgusto.repo/releases/" :username :env :passphrase :env}]
                 ["sonatype-staging"  {:url "https://oss.sonatype.org/content/groups/staging/"}]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [me.raynes/fs "1.4.5"]
                 [dsabanin/clj-yaml "0.4.1"]
                 [jenkins "0.1.0-SNAPSHOT"]
                 [crypto-random "1.1.0"]
                 [clj-webdriver "0.6.0"]
                 [amalloy/ring-gzip-middleware "0.1.3" :exclusions [org.clojure/clojure]]
                 [compojure "1.1.5"] ; Web routing https://github.com/weavejester/compojure
                 [com.taoensso/timbre "2.6.3"] ; Logging
                 [prismatic/plumbing "0.1.1"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/core.match "0.2.0"]
                 [korma "0.3.0-RC5"] ; ORM
                 [enlive "1.1.4"] ; serverside DOM manipulating
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [mysql/mysql-connector-java "5.1.26"]
                 [ring "1.2.1"]
                 [ring-anti-forgery "0.3.0"]
                 [ring-server "0.3.0" :exclusions [[org.clojure/clojure] [ring]]]
                 [ring-refresh "0.1.2" :exclusions [[org.clojure/clojure] [compojure]]]
                 [junit/junit "4.11"]
                 [lein-junit "1.1.4"]
                 [shoreleave "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [com.palletops/pallet-jclouds "1.7.3"]
;;                 [org.cloudhoist/pallet-jclouds "1.5.2"]
                 [org.jclouds/jclouds-all "1.5.5"]
                 [org.jclouds.driver/jclouds-slf4j "1.4.2"
                  :exclusions [org.slf4j/slf4j-api]]
                 [org.jclouds.driver/jclouds-sshj "1.4.2"]
                 [org.slf4j/jcl-over-slf4j "1.7.3"]
                 ]
  :plugins [[lein-ancient "0.5.4"]
            [lein-marginalia "0.7.1"]
            [lein-test-out "0.3.0"]
            [lein-ring "0.8.5"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]
            [lein-localrepo "0.4.1"]
            [s3-wagon-private "1.1.2"]
            [lein-expectations "0.0.8"]
            [lein-autoexpect "0.2.5"]]
  :profiles  {:pallet {:dependencies [[com.palletops/pallet "0.8.0-RC.9"]]}
              :dev {:dependencies [[ring-mock "0.1.5"]
                                   [ring/ring-devel "1.2.1"]
                                   [clj-webdriver "0.6.0"]
                                   [lein-autodoc "0.9.0"]
                                   [expectations "1.4.56"]
                                   [org.clojure/tools.trace "0.7.6"]
                                   [vmfest "0.3.0-rc.1"]]}})
;;            [org.thelastcitadel/jenkins-clojure-injector "0.2.1"]
;;  :jenkins-inject cloudseed.core/main
