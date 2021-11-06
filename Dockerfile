FROM adoptopenjdk/openjdk11:alpine-jre

# define env variable $SERVICE_NAME
ENV SERVICE_NAME shq

# Create app directory on container
RUN mkdir -p /usr/opt/service

# Bundle app source
COPY target/shq-0.0.1-SNAPSHOT.jar /usr/opt/service/shq-0.0.1-SNAPSHOT.jar

# make port 8080 available
EXPOSE 8080

# when container starts running, what should start executing
ENTRYPOINT ["java", "-jar", "/usr/opt/service/shq-0.0.1-SNAPSHOT.jar"]
