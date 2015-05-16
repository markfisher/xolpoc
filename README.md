# XD on Lattice PoC

The following examples demonstrate running the *ticktock* stream (`time | log`). First, we show how to run them as standalone modules. Next, we show how to run each module as an independently scalable app (LRP) on Lattice.

## Pre-requisites

You need Java (8) to build this project. You also need some Spring Cloud connectors that are not yet released. To get those, you will also need Maven (3). Do this:

```
$ git clone https://github.com/spring-cloud/spring-cloud-lattice
$ cd spring-cloud-lattice && mvn install --settings=.settings.xml
$ git clone https://github.com/markfisher/receptor-client
$ cd receptor-client && ./gradlew install
```

## Running Standalone

1: start redis locally with `redis-server`

2: clone this repository

3: download [Spring XD](http://projects.spring.io/spring-xd/) and copy the modules to */opt/xd/modules*:

````
opt
└── xd
    └── modules
        ├── processor
        ├── sink
        └── source
````

4: build the jar (from the repository root):

````
./gradlew clean build
````

5: deploy the *ticktock* stream:

````
$ cd build/libs/
$ java -Dmodule=ticktock.sink.log.1 -jar xolpoc-0.0.1-SNAPSHOT.jar
$ java -Dmodule=ticktock.source.time.0 -Dserver.port=8081 -jar xolpoc-0.0.1-SNAPSHOT.jar
````

**NOTE: at least one of the module processes needs to have an explicit `server.port` property, as shown above, to avoid conflicts on the default (8080)**

See it working by watching the console in the first (sink) process, every second you see one of these:

```
...
[2015-05-15 13:34:25.267] boot - 28094  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-05-15 13:34:25
...
```

> NOTE: instead of `/opt/xd` you can use a local directory, e.g. a symlink from the XD distro to the current directory, and launch the apps with `--xdHome=<pathToXD>`.

## Running with Docker

1: ensure Docker is installed and run a private registry as described [here](http://lattice.cf/docs/private-docker-registry/).

2: start redis locally with `redis-server` if not already running

3: clone this repository if not already cloned

4: download [Spring XD](http://projects.spring.io/spring-xd/) and set $XD_HOME (should be the parent dir of the 'modules' dir).

5: provide the host machine's IP address for the redis host property in `src/main/resources/application.yml` (that is necessary since 'localhost' is different for the processes that are running within the Docker containers).

6: build the jar (from the repository root):

````
./gradlew clean build
````

7: build the Docker image (also from the repository root):

````
./dockerize.sh
````

8: push the Docker image to the private registry:

````
docker push 192.168.59.103:5000/xd-module
````

5: start the log sink module:

````
docker run -e "XD_MODULE=ticktock.sink.log.1" 192.168.59.103:5000/xd-module
````

6: start the time source module:

````
docker run -e "XD_MODULE=ticktock.source.time.0" 192.168.59.103:5000/xd-module
````

7: once it has fully started, in the output for the log sink module you should see the time printed each second, e.g.:

```
...
[2015-05-16 17:46:28.856] boot - 1  INFO [inbound.ticktock.0-redis:queue-inbound-channel-adapter1] --- ticktock: 2015-05-16 17:46:24
...
```

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
