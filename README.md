redis-sample-java
=================

Redis sample written in Java, using Jedis.

Pre-requisite : A local Redis server up and running.
This code does not delete, nor flush the DB, it should not delete anything.


1. Build a JAR file with mvn package
2. Execute the test with a local redis server

    java -jar target/redis-sample-1.0.jar --hostname localhost --port 6363


Author: Nicolas Martignole


