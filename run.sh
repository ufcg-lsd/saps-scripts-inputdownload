#!/bin/bash

## This script downloads the input data for later processing.

## Checking args
if [ $# -ne 4 ]
then
  echo "Usage: $0 /tmp/teste landsat_X PPPRRR YYYY-MM-DD"
  exit 1
fi

## args
ROOT_DIR=$1
IMAGE_DATASET=$2
IMAGE_PATHROW=$3
IMAGE_DATE=$4

# global variables
SANDBOX=$(pwd)

# folders
INPUTDOWNLOADING_DIR_PATH=$ROOT_DIR/inputdownloading

echo "Step 1. Download image and generate station data"
cd $SANDBOX/USGS
java -Dlog4j.configuration=file:/home/saps/USGS/config/log4j.properties -jar $SANDBOX/USGS/target/USGS-0.0.1-SNAPSHOT-jar-with-dependencies.jar $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $INPUTDOWNLOADING_DIR_PATH $INPUTDOWNLOADING_DIR_PATH
PROCESS_OUTPUT=$?

if [ $PROCESS_OUTPUT -eq 0 ]
then
  echo "Step 2. Generate metadata"
  bash $SANDBOX/generate_metadata.sh $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $INPUTDOWNLOADING_DIR_PATH
fi

exit $PROCESS_OUTPUT

## Exit code
# exit code `0` indicates a successful execution. Any other number indicates failure.
# In particular, `3` indicates that a Landsat image was not found for the given paramenters.
