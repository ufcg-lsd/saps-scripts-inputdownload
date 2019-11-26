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

echo "Step 1. Download image"
python $SANDBOX/download.py $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $INPUTDOWNLOADING_DIR_PATH
PROCESS_OUTPUT=$?

if [ ! $PROCESS_OUTPUT -eq 0 ]
then
  echo "ERROR - Step 1. Download image [EXIT CODE = $PROCESS_OUTPUT]"
  exit $PROCESS_OUTPUT
fi

echo "Step 2. Generate station data"
FILE_MTL=($INPUTDOWNLOADING_DIR_PATH/*_MTL.txt)
FILENAME="${FILE_MTL##*/}"
FILENAME_WITHOUT_EXTENSION="${FILENAME%_MTL*}"

bash $SANDBOX/get-station-data.sh $FILENAME_WITHOUT_EXTENSION $INPUTDOWNLOADING_DIR_PATH
PROCESS_OUTPUT=$?

if [ ! $PROCESS_OUTPUT -eq 0 ]
then
  echo "ERROR - Step 2. Generate station data [EXIT CODE = $PROCESS_OUTPUT]"
  exit $PROCESS_OUTPUT
fi

echo "Step 3. Download raster elevation"
wget -P $INPUTDOWNLOADING_DIR_PATH https://github.com/ThiagoWhispher/elevation-raster/raw/master/$IMAGE_PATHROW.tif
PROCESS_OUTPUT=$?

if [ ! $PROCESS_OUTPUT -eq 0 ]
then
  echo "ERROR - Step 3. Download raster elevation [EXIT CODE = $PROCESS_OUTPUT]"
  exit $PROCESS_OUTPUT
fi

echo "Step 4. Generate metadata"
bash $SANDBOX/generate_metadata.sh $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $INPUTDOWNLOADING_DIR_PATH
PROCESS_OUTPUT=$?

if [ ! $PROCESS_OUTPUT -eq 0 ]
then
  echo "ERROR - Step 4. Generate metadata [EXIT CODE = $PROCESS_OUTPUT]"
  exit $PROCESS_OUTPUT
fi

exit 0

## Exit code
# exit code `0` indicates a successful execution. Any other number indicates failure.
# In particular, `3` indicates that a Landsat image was not found for the given paramenters.
