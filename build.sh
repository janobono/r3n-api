#!/bin/sh
JAVA_HOME="$HOME/opt/jdk1.7.0_10/bin"
MAVEN_HOME="$HOME/opt/netbeans-7.2.1/java/maven/bin"
PATH=$JAVA_HOME:$MAVEN_HOME:$PATH

mvn -version

R3N="$HOME/workspace/r3n/trunk/r3n"

cd $R3N
mvn clean
mvn install
