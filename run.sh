#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Put args in quotes after ./run.sh.'
    exit 1
fi
export CLASSPATH=build/libs/aws_tagging-1.0.jar
echo "## arguments $@..."
gradle run --args="$@"