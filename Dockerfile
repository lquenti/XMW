FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

# Create directory and copy flush.xml for build stage
RUN mkdir -p /root/xmw_data
COPY EXA/flush.xml /root/xmw_data/flush.xml

# Set JAVA_HOME explicitly
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN mvn clean package

FROM tomcat:10.1-jdk17

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory for XML data
RUN mkdir -p /root/xmw_data

# Copy the WAR files from builder stage
COPY --from=builder /app/EXA/target/exa.war /usr/local/tomcat/webapps/exa.war
COPY --from=builder /app/StudIP/target/studip.war /usr/local/tomcat/webapps/studip.war
COPY --from=builder /app/User/target/user.war /usr/local/tomcat/webapps/user.war

# Copy the flush.xml file
COPY --from=builder /root/xmw_data/flush.xml /root/xmw_data/flush.xml

EXPOSE 8080

VOLUME ["/root/xmw_data"]

CMD ["catalina.sh", "run"]