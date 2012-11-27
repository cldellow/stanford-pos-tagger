#!/bin/bash

rm -f postagger.jar
#ln -s ./stanford-postagger-release.jar postagger.jar
ln -s ./stanford-postagger.jar postagger.jar
CP=./:./postagger.jar:/usr/share/scala/lib/scala-library.jar:/usr/local/scala-2.9.2/lib/scala-library.jar

#-agentlib:hprof=file=c:/hprof.txt,cpu=times
#scalac -cp $CP Go.scala && java -agentlib:hprof=file=./hprof.txt,cpu=samples,interval=2,depth=20 -cp $CP MyScalaApp
scalac -cp $CP Go.scala && java -cp $CP MyScalaApp
