FROM maven:3-jdk-11

ADD . /usr/src/app
WORKDIR /usr/src/app
RUN git submodule update --init --recursive
RUN mvn clean install
