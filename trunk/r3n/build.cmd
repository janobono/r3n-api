@echo off
SET JAVA_HOME="C:\Progra~1\Java\jdk1.7.0_21"
SET MAVEN_HOME="C:\Progra~1\NetBeans 7.3.1\java\maven\bin"
SET Path=%Path%;%JAVA_HOME%;%MAVEN_HOME%
SET MYPATH=%CD% 

mvn clean install -DskipTests
