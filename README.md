# XD on Lattice PoC

The following examples demonstrate running the *ticktock* stream (`time | log`). First, we show how to run them as standalone modules. Next, we show how to run each module as an independently scalable app (LRP) on Lattice.

## Running Standalone

1: clone this repository

2: copy modules to */opt/xd/modules*:

````
opt
└── xd
    └── modules
        ├── processor
        ├── sink
        └── source
````

3: build the jar (from the repository root):

````
./gradlew clean build
````

4: deploy the *ticktock* stream:

````
$ cd build/libs/
$ java -Dmodule=ticktock.sink.log.1 -jar xolpoc-0.0.1-SNAPSHOT.jar
$ java -Dmodule=ticktock.source.time.0 -Dserver.port=8081 -jar xolpoc-0.0.1-SNAPSHOT.jar
````

**NOTE: at least one of the module processes needs to have an explicit `server.port` property, as shown above, to avoid conflicts on the default (8080)**

## Running on Lattice

1: launch lattice with vagrant as described [here](https://github.com/cloudfoundry-incubator/lattice#launching-with-vagrant)

2: start redis-server on the host machine

3: start the log sink module:

````
$ ltc start xd-sink pperalta/xd -- java -Dmodule=ticktock.sink.log.1 -Dspring.redis.host=<host-ip> -Dserver.port=9999 -jar /opt/xd/lib/xolpoc-0.0.1-SNAPSHOT.jar
````

4: start the time source module:

````
$ ltc start xd-source pperalta/xd -- java -Dmodule=ticktock.source.time.0 -Dspring.redis.host=<host-ip> -Dserver.port=8888 -jar /opt/xd/lib/xolpoc-0.0.1-SNAPSHOT.jar
````

5: scale the log module up to 3 instances:

````
$ ltc scale xd-sink 3
````

6: tail the xd-sink logs:

````
$ ltc logs xd-sink
````

you should see time messages logged on all 3 instances (omitting health check messages):

````
22 Feb 13:04 [APP|0] [2015-02-22 18:04:04.356] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:04
22 Feb 13:04 [APP|1] [2015-02-22 18:04:05.361] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:05
22 Feb 13:04 [APP|1] [2015-02-22 18:04:06.365] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:06
22 Feb 13:04 [APP|0] [2015-02-22 18:04:07.369] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:07
22 Feb 13:04 [APP|0] [2015-02-22 18:04:08.372] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:08
22 Feb 13:04 [APP|1] [2015-02-22 18:04:09.377] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:09
22 Feb 13:04 [APP|1] [2015-02-22 18:04:10.381] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:10
22 Feb 13:04 [APP|2] [2015-02-22 18:04:11.662] boot - 10  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-02-22 18:04:11
````
