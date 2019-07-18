# 1 = scene_id
# 2 = product_id
# 10 = wrs_path
# 11 = wrs_row
# 12 = cloud_cover
# 18 = download_url

curl 'https://storage.googleapis.com/gcp-public-data-landsat/index.csv.gz' --output index.csv.gz
gunzip index.csv.gz

index_csv="index.csv"
output="scene_list"

rm -f $output

cut -d, -f1,10,11,12,18 $index_csv | sed 's/gs:\/\//http:\/\/storage.googleapis.com\//g' | awk -vulx=$1 -vuly=$2 -vbrx=$3 -vbry=$4 -F','  '{
    if($1 != "SCENE_ID") {
        if(ulx <= $2 && $2 <= brx && uly <= $3 && $3 <= bry)
            print $1 "," $2 "," $3 "," $4 "," $5
    } else {
        print "SCENE_ID,PATH,ROW,CLOUD_COVER,DOWNLOAD_URL"
    }
}' > $output

rm $index_csv
