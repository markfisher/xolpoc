XD on Lattice PoC

1. clone this repo.

2. copy modules to */opt/xd/modules*:

````
opt
└── xd
    └── modules
        ├── processor
        ├── sink
        └── source
````

3. build the jar (from root of repo):

````
./gradlew clean build
````

4. run (at least one needs to have an explicit server.port to avoid conflicts on the default, which is 8080):

````
$ cd build/libs/
$ java -Dmodule=ticktock.sink.log.1 -jar xolpoc-0.0.1-SNAPSHOT.jar
$ java -Dmodule=ticktock.source.time.0 -Dserver.port=8081 -jar xolpoc-0.0.1-SNAPSHOT.jar
````
