# DNS Client

The script to start the dns client can be found in the dns-client/target directory. The command to execute this function is:

	bash run.sh

You must be in the same directoy to run the script. 

It will open a GUI that will prompt you to input an identfier. It defaults to your IP address, but any value would work. This identifier allows for you to reconnect to the same file if you ever become disconnected during the packet capture. 

If you experience any difficulty or get any pop-ups, it is most likely an indication that the java process has crashed. We should be keeping an eye on it over the next few days to mitigate the chance of this happening. 


# EC2 Server

As briefly mentioned above for it's above par track record, this server runs remotely on an EC2 instance. It uses an elastic IP address to keep configurations simple. The command to execute the program is:

	java -cp target/dependency/*:target/goodPaulHuntingAJauntIntoDNS-0.0.1-SNAPSHOT.jar listener.main.Main

If you wanted to ssh into the server to see some of the code we can easily grant that privelege. 


# MapReduce

The map reduce job runs on a 4 node cluster - 3 slaves and 1 master. Currently, the process is designed to run once daily on an aggregate amount of data. The code to execute the 

# Map Reduce and Web Server