#!/bin/bash

## Objetive: Download the input data for processing
## For example: $0 landsat_5 215065 2004-12-01 /tmp/output /tmp/metadata

## Checking args
if [ $# -ne 5 ]
then
  echo "Usage: $0 landsat_X PPPRRR YYYY-MM-DD output_path metadata_path"
  exit 1
fi

## Capture args
IMAGE_DATASET=$1
IMAGE_PATHROW=$2
IMAGE_DATE=$3
OUTPUT_DIR_PATH=$4
METADATA_DIR_PATH=$5

# Global variables
SANDBOX=$(pwd)

echo "Step 1. Download image and generate station data"
python download.py $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $OUTPUT_DIR_PATH
PROCESS_OUTPUT=$?

if [ $PROCESS_OUTPUT -eq 0 ]
then
  echo "Step 2. Generate metadata"
  bash $SANDBOX/generate_metadata.sh $IMAGE_DATASET $IMAGE_PATHROW $IMAGE_DATE $OUTPUT_DIR_PATH $METADATA_DIR_PATH
fi

exit $PROCESS_OUTPUT

## Exit code
# This script should have the following return pattern:
# - `0` represents a successful execution.
# - `3` represents that the Landsat image was not found.
# And any other exit code will be considered failed.
