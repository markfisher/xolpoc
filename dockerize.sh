#!/bin/bash

if [ ! -f "Dockerfile" ]; then
	echo "Dockerfile not found"
	exit 1
fi

if [ -z "$XD_HOME" ]; then
	echo "XD_HOME must be set"
	exit 1
fi

if [ ! -d "artifacts" ]; then
	mkdir artifacts
fi

if [ ! -f "build/libs/xolpoc-0.0.1-SNAPSHOT.jar" ]; then
	echo "JAR not available; run gradlew build first"
	exit 1
fi

cp -r $XD_HOME/modules artifacts

docker build -t 192.168.59.103:5000/xd-module .
