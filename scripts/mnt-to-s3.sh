#!/bin/bash
CUR_DIR=/root/goodPaulHuntingAJauntIntoDNS/scripts
S3_DIR=s3://cs-514/temp
MNT_DIR=/mnt/temp
FILE=dns_queries.txt
LOG_FILE=$CUR_DIR/../logs/mnt-to-s3-output-log.txt

run() {		
	# Move from s3 to local directory
	write_to_log "COPYING" "Copying $FILE from mnt to s3."
	/usr/local/bin/aws s3 cp "${MNT_DIR}/${FILE}" ${S3_DIR} > /dev/null 2>&1

	# Erase the dns_queries file
	write_to_log "ERASING" "ERASING contents from $FILE."
	sudo echo '' > "${MNT_DIR}/${FILE}"
}

write_to_log() {
	TIME=$(date +"%m-%d-%Y %T")
        echo "$TIME [$1]: $2" >> $LOG_FILE
}

run