#!/bin/sh
SERVICE_NAME=securities
PATH_TO_JAR=/home/pi/repos/securities/build/libs/securities-0.0.1-SNAPSHOT.jar 
PID_PATH_NAME=/tmp/securities-pid 
case $1 in     
	start)         
		echo "Starting $SERVICE_NAME ..."         
		if [ ! -f $PID_PATH_NAME ]; then             
			nohup java -jar -Dspring.profiles.active=pi $PATH_TO_JAR --debug /tmp 2>> /dev/null >> /dev/null &
			echo $! > $PID_PATH_NAME             
			echo "$SERVICE_NAME started ..."         
		else             
			echo "$SERVICE_NAME is already running ..."         
		fi     
	;;     
	stop)         
		if [ -f $PID_PATH_NAME ]; then             
			PID=$(cat $PID_PATH_NAME);             
			echo "$SERVICE_NAME stoping ..."             
			kill $PID;             
			echo "$SERVICE_NAME stopped ..."             
			rm $PID_PATH_NAME         
		else             
			echo "$SERVICE_NAME is not running ..."         
		fi     
	;;     
	restart)         
		if [ -f $PID_PATH_NAME ]; then             
			PID=$(cat $PID_PATH_NAME);             
			echo "$SERVICE_NAME stopping ...";             
			kill $PID;             
			echo "$SERVICE_NAME stopped ...";             
			rm $PID_PATH_NAME             
			echo "$SERVICE_NAME starting ..."             
			nohup java -Dspring.profiles.active=pi -jar $PATH_TO_JAR --debug /tmp 2>> /dev/null >> /dev/null &             
			echo $! > $PID_PATH_NAME             
			echo "$SERVICE_NAME started ..."         
		else             
			echo "$SERVICE_NAME is not running ..."         
		fi     
	;; 
esac

