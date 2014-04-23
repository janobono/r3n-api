#!/bin/sh
JAVA_HOME="$HOME/opt/jdk1.7.0_21"
MAVEN_HOME="$HOME/opt/netbeans-7.3.1/java/maven"
PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

mvn -version
mvn clean
mvn install
