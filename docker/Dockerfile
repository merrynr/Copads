# Ubuntu 18.04 with JDK 8
# Build image with:  docker build -t csci251:latest .

FROM ubuntu:18.04
MAINTAINER Peizhao Hu, http://cs.rit.edu/~ph
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y net-tools iputils-ping maven gradle nmap wget git vim build-essential && \
    apt-get clean

RUN mkdir /csci251
WORKDIR /csci251

RUN git clone https://gitlab.com/SpiRITlab/CoPaDS.git /csci251/.

