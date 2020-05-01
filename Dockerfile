FROM ubuntu:16.04

RUN apt-get update
RUN apt-get install wget

WORKDIR /home/saps

COPY . .
