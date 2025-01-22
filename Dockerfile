FROM openjdk:17
COPY target/cloud-gateway-0.0.1-SNAPSHOT.jar cloud-gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/cloud-gateway.jar"]
