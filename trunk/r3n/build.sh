#!/bin/sh
JAVA_HOME="$HOME/opt/jdk1.7.0_21/bin"
MAVEN_HOME="$HOME/opt/netbeans-7.3.1/java/maven/bin"
PATH=$JAVA_HOME:$MAVEN_HOME:$PATH

mvn -version
mvn clean
mvn install
