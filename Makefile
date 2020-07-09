all: target/aws-tagging-1.0.jar

target/aws-tagging-1.0.jar: src/main/java/org/globant/*.java
	mvn package

clean:
	mvn clean