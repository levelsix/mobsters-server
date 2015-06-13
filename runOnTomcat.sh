#!/bin/bash

tomcatWebappsDir=/Library/Tomcat/webapps

echo "Stopping tomcat"
/Library/Tomcat/bin/shutdown.sh; sleep 5

echo "Removing old webapp from $tomcatWebappsDir"
rm -rf $tomcatWebappsDir/*

echo "Copying new war to $tomcatWebappsDir"
cp target/mobsters-server-1.0-SNAPSHOT.war $tomcatWebappsDir/ROOT.war

echo "Starting tomcat"
/Library/Tomcat/bin/startup.sh

