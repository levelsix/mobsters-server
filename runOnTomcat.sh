#!/bin/bash

tomcatWebappsDir=/Library/Tomcat/webapps

echo "Stopping tomcat"
/Library/Tomcat/bin/shutdown.sh

echo "Removing old webapp from $tomcatWebappsDir"
sudo rm -rf $tomcatWebappsDir/*

echo "Copying new war to $tomcatWebappsDir
sudo cp target/mobsters-1.0-SNAPSHOT.war $tomcatWebappsDir/

echo "Starting tomcat"
/Library/Tomcat/bin/startup.sh

