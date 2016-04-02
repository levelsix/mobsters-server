mvn clean package -P user-ash -DskipTests

rm -r /usr/local/Cellar/tomcat/8.0.32/libexec/webapps/*
cp mobsters-webapp/target/mobsters-webapp-1.0-SNAPSHOT.war /usr/local/Cellar/tomcat/8.0.32/libexec/webapps/ROOT.war

java -Djava.library.path=~/dynamo/DynamoDBLocal_lib -jar ~/dynamo/DynamoDBLocal.jar -sharedDb &

brew services start mysql

catalina run

