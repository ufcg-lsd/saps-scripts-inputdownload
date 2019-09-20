#!/bin/bash
IMAGE_NAME=$1
IMAGES_DIR_PATH=$2

IMAGE_MTL_PATH=$IMAGES_DIR_PATH/$IMAGE_NAME"_MTL.txt"
IMAGE_STATION_FILE_PATH=$IMAGES_DIR_PATH/$IMAGE_NAME"_station.csv"

# Global variables
SANDBOX=$(pwd)
SEBAL_DIR_PATH=$SANDBOX/SEBAL
CONF_FILE=sebal.conf
LIBRARY_PATH=/usr/local/lib
BOUNDING_BOX_PATH=example/boundingbox_vertices
LOG4J_PATH=$SEBAL_DIR_PATH/log4j.properties

# This function calls a java code to prepare a station file of a given image
function getStationData {
  cd $SEBAL_DIR_PATH

  echo "Pre Process Parameters: $IMAGE_NAME $IMAGES_DIR_PATH/ $IMAGE_MTL_PATH $IMAGES_DIR_PATH/ 0 0 9000 9000 1 1 $SEBAL_DIR_PATH/$BOUNDING_BOX_PATH $SEBAL_DIR_PATH/$CONF_FILE"
  java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n -Dlog4j.configuration=file:$LOG4J_PATH -Djava.library.path=$LIBRARY_PATH -cp target/SEBAL-0.0.1-SNAPSHOT.jar:target/lib/* org.fogbowcloud.sebal.PreProcessMain $IMAGE_NAME $IMAGES_DIR_PATH/ $IMAGE_MTL_PATH $IMAGES_DIR_PATH/ 0 0 9000 9000 1 1 $SEBAL_DIR_PATH/$BOUNDING_BOX_PATH $SEBAL_DIR_PATH/$CONF_FILE
  mv $IMAGES_DIR_PATH/$IMAGE_NAME/$IMAGE_NAME"_station.csv" $IMAGES_DIR_PATH
  cd $IMAGES_DIR_PATH
  rm -r $IMAGE_NAME

  cd $SEBAL_DIR_PATH
  chmod 777 $IMAGE_STATION_FILE_PATH
  echo -e "\n" >> $IMAGE_STATION_FILE_PATH
  cd ..
}

getStationData
