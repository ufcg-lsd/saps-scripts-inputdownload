FROM ubuntu

WORKDIR /home/ubuntu

RUN mkdir config

COPY config/ ./config
COPY ./ ./

RUN apt-get update -y
RUN apt-get install git -y
RUN apt-get install nano -y
RUN apt-get install software-properties-common -y
RUN apt-get install curl -y
RUN add-apt-repository ppa:openjdk-r/ppa -y
RUN apt-get update -y
RUN apt-get install openjdk-8-jdk -y
RUN apt-get update -y
RUN apt-get install wget -y
RUN apt-get install maven -y

#RUN cd /home/ubuntu/ && git clone https://github.com/fogbow/SEBAL.git
#RUN cd /home/ubuntu/SEBAL && git checkout update-station-search && mvn -e install -Dmaven.test.skip=true
