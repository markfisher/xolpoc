# XD on Lattice PoC

## Quick Start

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

4: deploy a "stream" by running 2 modules, e.g. `time | log`:

````
$ cd build/libs/
$ java -Dmodule=ticktock.sink.log.1 -jar xolpoc-0.0.1-SNAPSHOT.jar
$ java -Dmodule=ticktock.source.time.0 -Dserver.port=8081 -jar xolpoc-0.0.1-SNAPSHOT.jar
````

**NOTE: at least one of the module processes needs to have an explicit `server.port` property, as shown above, to avoid conflicts on the default (8080)**
