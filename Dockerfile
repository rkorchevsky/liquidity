# Alpine Linux with OpenJDK JRE
FROM openjdk:10-jre-alpine

# copy WAR into image
COPY onytrex-liquidity.jar /app.jar

# run application with this command line
CMD ["/usr/bin/java", "-jar", "/app.jar"]
