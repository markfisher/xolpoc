#
# Dockerfile to build the Spring XD Module image
#

FROM java:8
VOLUME /tmp
RUN mkdir -p /opt/xd/modules
ADD artifacts/modules /opt/xd/modules
ADD build/libs/xolpoc-0.0.1-SNAPSHOT.jar xd-module.jar
RUN bash -c 'touch /xd-module.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/xd-module.jar"]
