1) To use protoc, go to top of project and type
protoc -I=src/main/java/com/lvl6/proto/ --java_out=src/main/java/ src/main/java/com/lvl6/proto/*.proto



7) Building with Maven

Install Maven and m2eclipse

http://maven.apache.org/download.html

http://eclipse.org/m2e/

cd to root of project and run:

./installJavaJsonToLocalMaven.sh 
mvn clean
mvn install



8) Building with Maven in Eclipse

After installing m2eclipse

Import Project > Maven > Existing Maven project

Choose Utopia folder root

Select the project in the tree and OK



9) Tomcat

Install tomcat7

Stop tomcat

remove all files from tomcatroot/webapps/

copy utopia/target/utopia-server-1.0-SNAPSHOT.war to tomcatroot/webapps/ROOT.war

Start tomcat






