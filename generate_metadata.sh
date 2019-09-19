#!/bin/bash

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

METADATA_FILE_PATH=$METADATA_DIR_PATH/application.metadata
rm -rf $METADATA_FILE_PATH
touch $METADATA_FILE_PATH

MTL_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_MTL.txt")
LANDSAT_SCENE_ID=$(cat $MTL_OUTPUT_FILE_PATH | grep LANDSAT_SCENE_ID)
TYPE_L8_OUTPUT_FILE=$(echo $LANDSAT_SCENE_ID | grep LC8)
TYPE_L7_OUTPUT_FILE=$(echo $LANDSAT_SCENE_ID | grep LE7)
TYPE_L5_OUTPUT_FILE=$(echo $LANDSAT_SCENE_ID | grep LT5)

B1_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B1.TIF")
B2_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B2.TIF")
B3_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B3.TIF")
B4_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B4.TIF")
B5_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B5.TIF")
B6_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B6.TIF")
B6_VCID_1_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B6_VCID_1.TIF")
B6_VCID_2_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B6_VCID_2.TIF")
B7_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B7.TIF")
B8_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B8.TIF")
B9_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B9.TIF")
B10_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B10.TIF")
B11_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_B11.TIF")
BQA_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_BQA.TIF")
STATION_OUTPUT_FILE_PATH=$(find $OUTPUT_DIR_PATH -iname "*_station.csv")

CURRENT_DATE=$(date)

echo "# Inputdownload (googleapis) Implementation Metadata" >> $METADATA_FILE_PATH
echo "$CURRENT_DATE # Date" >> $METADATA_FILE_PATH

echo "INPUT" >> $METADATA_FILE_PATH
echo "$IMAGE_DATASET # Image Dataset" >> $METADATA_FILE_PATH
echo "$IMAGE_PATHROW # Image Pathrow" >> $METADATA_FILE_PATH
echo "$IMAGE_DATE # Image date" >> $METADATA_FILE_PATH

echo "OUTPUT" >> $METADATA_FILE_PATH
echo "$B1_OUTPUT_FILE_PATH # Band 1 from image" >> $METADATA_FILE_PATH
echo "$B2_OUTPUT_FILE_PATH # Band 2 from image" >> $METADATA_FILE_PATH
echo "$B3_OUTPUT_FILE_PATH # Band 3 from image" >> $METADATA_FILE_PATH
echo "$B4_OUTPUT_FILE_PATH # Band 4 from image" >> $METADATA_FILE_PATH
echo "$B5_OUTPUT_FILE_PATH # Band 5 from image" >> $METADATA_FILE_PATH
if [ "$TYPE_L5_OUTPUT_FILE" ]
then
  echo "$B6_OUTPUT_FILE_PATH # Band 6 from image" >> $METADATA_FILE_PATH
fi
if [ "$TYPE_L7_OUTPUT_FILE" ]
then
  echo "$B6_VCID_1_OUTPUT_FILE_PATH # Band 6 VCID 1 from image" >> $METADATA_FILE_PATH
  echo "$B6_VCID_2_OUTPUT_FILE_PATH # Band 6 VCID 2 from image" >> $METADATA_FILE_PATH
fi
if [ "$TYPE_L8_OUTPUT_FILE" ]
then
  echo "$B6_OUTPUT_FILE_PATH # Band 6 from image" >> $METADATA_FILE_PATH
fi

echo "$B7_OUTPUT_FILE_PATH # Band 7 from image" >> $METADATA_FILE_PATH

if [ "$TYPE_L7_OUTPUT_FILE" ]
then
  echo "$B8_OUTPUT_FILE_PATH # Band 8 from image" >> $METADATA_FILE_PATH
fi
if [ "$TYPE_L8_OUTPUT_FILE" ]
then
  echo "$B8_OUTPUT_FILE_PATH # Band 8 from image" >> $METADATA_FILE_PATH
  echo "$B9_OUTPUT_FILE_PATH # Band 9 from image" >> $METADATA_FILE_PATH
  echo "$B10_OUTPUT_FILE_PATH # Band 10 from image" >> $METADATA_FILE_PATH
  echo "$B11_OUTPUT_FILE_PATH # Band 11 from image" >> $METADATA_FILE_PATH
fi
echo "$BQA_OUTPUT_FILE_PATH # Band QA from image" >> $METADATA_FILE_PATH
echo "$MTL_OUTPUT_FILE_PATH # MTL from image" >> $METADATA_FILE_PATH
echo "$STATION_OUTPUT_FILE_PATH # Station data from image" >> $METADATA_FILE_PATH
