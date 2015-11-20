#!/bin/bash
HOME=".."
PYTHON_TO_HOME="../../.."
PYTHON_DIR="dns_client/client/main"
PYTHON_SCRIPT="query_generator.py"
JAR="target/goodPaulHuntingAJauntIntoDNS-0.0.1-SNAPSHOT.jar"
FILE_DIR="files"
FILE="dns_queries.txt"
LOG_FILE="logs/dns-to-ec2-output.txt"

run() {
	cd $HOME
	# Run the python command
	write_to_log "PYTHON" "Generating new file."
	cd $PYTHON_DIR
	python $PYTHON_SCRIPT > /dev/null 2>&1

	# Move the output to the correct folder
	cd $PYTHON_TO_HOME
	write_to_log "COPYING" "Moving file to folder."
	cp "$PYTHON_DIR/$FILE" "$FILE_DIR/" > /dev/null 2>&1

	# Move into the correct folder and Execute the command
	write_to_log "REPLACING" "Replacing the jar file."
	cd $FILE_DIR
	jar uf $JAR $FILE > /dev/null 2>&1

	# Move to the home dir and run the command
	cd $HOME
    write_to_log "JAVA" "Running the java file." 
	java -cp target/dependency/*:target/goodPaulHuntingAJauntIntoDNS-0.0.1-SNAPSHOT.jar client.dns.DNSClientSender >> $LOG_FILE 2>&1
	write_to_log "JAVA" "Ending the the java file." 

}

write_to_log() {
	TIME=$(date +"%m-%d-%Y %T")
        echo "$TIME [$1]: $2" >> $LOG_FILE
}

run