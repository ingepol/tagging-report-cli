#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Put args in quotes after ./run.sh.'
    exit 1
fi
export CLASSPATH=target/aws_tagging-1.0.jar
echo "## arguments $@..."
mvn exec:java -Dexec.mainClass="org.globant.Application" -Dexec.args="$@" -Dexec.cleanupDaemonThreads=false
