# syntax=docker/dockerfile:1
FROM adoptopenjdk/openjdk16:x86_64-centos-jdk-16.0.1_9
RUN yum install -y git
RUN git clone https://github.com/tino1b2be/kotLOB.git
RUN cd /kotLOB && bash gradlew clean build
CMD cd /kotLOB && bash gradlew run