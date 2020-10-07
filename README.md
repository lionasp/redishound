# RedisHound
Simple GUI Redis client

### How to run

1. Install [maven](http://maven.apache.org/)
2. In project directory (near the `pom.xml` file) execute:
```shell script
mvn javafx:run

# or to get a .jar file:

mvn clean package assembly:single
java -jar target/redishound-1.0-SNAPSHOT-jar-with-dependencies.jar
```