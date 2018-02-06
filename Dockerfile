FROM maven:3-jdk-8

ADD . /usr/src/app
WORKDIR /usr/src/app
RUN git submodule update --init --recursive
RUN mvn clean install
