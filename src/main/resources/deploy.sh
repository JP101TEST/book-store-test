#!/bin/bash

APP_DIR=~/app/back-end
JAR=$APP_DIR/backend_aws.jar
JAVA=$APP_DIR/jdk21/bin/java
LOG=$APP_DIR/app.log

mv -f ~/dist/* "$APP_DIR"/

pkill -f backend_aws.jar || true
sleep 2

cd "$APP_DIR"
nohup "$JAVA" -jar "$JAR" > "$LOG" 2>&1 < /dev/null &
disown

sleep 5
if pgrep -f backend_aws.jar > /dev/null; then
    echo "backend_aws started (pid: $(pgrep -f backend_aws.jar))"
    exit 0
else
    echo "backend_aws failed to start. Last log:"
    tail -n 30 "$LOG"
    exit 1
fi