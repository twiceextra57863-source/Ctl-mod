#!/bin/sh
##############################################################################
##  Gradle start script for UN*X systems.
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$')`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done
APP_HOME=`cd "$(dirname "$PRG")"; pwd -P`/gradle/wrapper
APP_NAME="Gradle"
CLASSPATH=$APP_HOME/gradle-wrapper.jar

exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
