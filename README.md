 # Gatling DSE Stress Simulation Catalog
The goal of the repo is to provide a sample of the [Gatling DSE Stress](https://github.com/datastax/gatling-dse-stress/) Framework's usage. Feel free to submit a pull request with example simulations.

# Using the Examples

## Building
To build a jar run `sbt clean assembly`.  The compiled jar will be found in `gatling-dse-simcatalog_2.12-1.2.3-SNAPSHOT.jar` and the executable app will be at `target/scala-2.12/gatling-dse-sims`


## Running a Simulation
First build the executable app using `sbt clean compile` then run the app with the path or name of wanted sim name `gatling-dse-sims run {SimName}`.  

Example: `target/scala-2.12/gatling-dse-sims run WriteOrderSimulation`


## Configuration
Project configs can be found in the `src/main/resources` the `application.conf` is the file to set the Simulation and Cassandra settings.  

During run you can override part or all of the application settings by using `-Dconfig.file={filePath}`.  If you want to override a single setting only just use the path of the config ie `-Dcassandra.hosts=127.0.0.1`.  This single setting can be used for any value in the `gatling.conf` file as well.


To change configurations on the fly with excutable use the following format:
`JAVA_OPTS="-Dcassandra.hosts=127.0.0.1" gatling-dse-sims run WriteOrderSimulation`

To view all loaded and overridden configurations run the follow:
`gatling-dse-sims showConf <all, general, cassandra, simulations, gatling>`

See: [Gatling DSE Stress Wiki](https://github.com/datastax/gatling-dse-stress/wiki) for more specific docs for usage.


### Listing Available Sims in Jar
Run `target/scala-2.12/gatling-dse-sims listSims` or `build/libs/gat

### Showing Default Configurations
Run `target/scala-2.12/gatling-dse-sims showConf <all, general, cassandra, simulations, gatling>`


# Requirements
- Java 1.8+
- SBT

Running `sbt assembly` will download all of the needed libraries including Scala to your local machine.

# Questions or Requests
Please use the [Issues section](https://github.com/datastax/gatling-dse-stress/issues) to add any questions on usage or requests

Use `#gatling-dse` Slack channel to ask questions.

### Contributing
The easiest way to contribute is to:
1. Clone this project
1. Create a new branch w/ your name and simple desc. Example: username-oauth
1. Push your branch to the repo
1. Create a new pull request against master

The new pull request will be reviewed, tested and if verified will be merged with the _master_ branch.

#### Requirements of New Contributions
Please include the table creation in the Actions class for your under _createTables()_ or _initGraphSchema_ to let other users ramp up quickly with using you simulations. A short description of the simlation in the doc comments is also welcomed.


## Contributions

It has been developped by Brad Vernon ([ibspoof](https://github.com/ibspoof)) and improved by the following contributors:

* Matt Overstreet ([omnifroodle](https://github.com/omnifroodle))
* Chris Bradford ([bradfordcp](https://github.com/bradfordcp))
