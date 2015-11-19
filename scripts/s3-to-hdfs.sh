#!/bin/bash
CUR_DIR=/root/goodPaulHuntingAJauntIntoDNS/scripts
S3_DIR=s3://cs-514/temp
HDFS_DIR=/user/hadoop
FILE=dns_queries.txt
LOG_FILE=$CUR_DIR/../logs/output-log.txt

run() {
	# Log the action
	write_to_log "COPYING" "Copying $FILE from s3, moving to hdfs."
		
	# Move from s3 to local directory
	/usr/local/bin/aws s3 cp "${S3_DIR}/${FILE}" $CUR_DIR/ > /dev/null 2>&1
	
	# Delete the file from s3, catch the output	
	write_to_log "DELETING" "Removing file $FILE from hdfs."
	/root/ephemeral-hdfs/bin/hadoop fs -rm "${HDFS_DIR}/${FILE}" > /dev/null 2>&1

	# Add the file to hdfs
	write_to_log "WRITING" "Writing $FILE to hdfs."
	/root/ephemeral-hdfs/bin/hadoop fs -put "${CUR_DIR}/${FILE}" "${HDFS_DIR}/${FILE}" > /dev/null 2>&1

	# Clean up the local file
	rm "${CUR_DIR}/${FILE}" > /dev/null 2>&1
}

write_to_log() {
	TIME=$(date +"%m-%d-%Y %T")
        echo "$TIME [$1]: $2" >> $LOG_FILE
}

run