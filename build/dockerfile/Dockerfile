FROM openjdk:11-jdk-slim

COPY ../../target/cryptocurrency-loader-fat.jar /cryptocurrency-loader.jar

EXPOSE 8080

CMD ["java", "-jar", "cryptocurrency-loader.jar"]