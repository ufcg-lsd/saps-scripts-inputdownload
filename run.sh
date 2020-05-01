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

# folders
INPUTDOWNLOADING_DIR_PATH=$ROOT_DIR/inputdownloading

# repo with results
REPO=http://www2.lsd.ufcg.edu.br/~thiagoyes/saps/nop-download-files/inputdownloading

cd $INPUTDOWNLOADING_DIR_PATH

# download files
wget $REPO/215065.tif
wget $REPO/error.log
wget $REPO/out.log
wget $REPO/stage.metadata
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B1.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B2.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B3.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B4.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B5.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B6.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B7.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B8.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B9.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B10.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_B11.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_BQA.TIF
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_MTL.txt
wget $REPO/LC08_L1TP_215065_20150623_20170407_01_T1_station.csv

exit 0

## Exit code
# exit code `0` indicates a successful execution. Any other number indicates failure.
# In particular, `3` indicates that a Landsat image was not found for the given paramenters.
