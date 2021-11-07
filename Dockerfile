FROM maven:3.8.3-jdk-11 as build

COPY src /home/app/src

COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package

FROM adoptopenjdk/openjdk11:alpine-jre

# define env variable $SERVICE_NAME
ENV SERVICE_NAME shq

# Create app directory on container
RUN mkdir -p /usr/opt/service

# Bundle app source
COPY --from=build /home/app/target/shq-0.0.1-SNAPSHOT.jar /usr/opt/service/shq-0.0.1-SNAPSHOT.jar

# make port 8080 available
EXPOSE 8080

# when container starts running, what should start executing
ENTRYPOINT ["java", "-jar", "/usr/opt/service/shq-0.0.1-SNAPSHOT.jar"]
