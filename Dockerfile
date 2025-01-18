FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY . .

# downgrade jdk from 23 to 21
RUN mvn clean package -Dmaven.compiler.source=21 -Dmaven.compiler.target=21


# Create directory and copy flush.xml for build stage
RUN mkdir -p /root/xmw_data
COPY EXA/flush.xml /root/xmw_data/flush.xml

# Set JAVA_HOME explicitly
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN mvn clean package

FROM tomcat:11.0-jdk21

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory for XML data
RUN mkdir -p /root/xmw_data

# Configure CORS
RUN echo '<?xml version="1.0" encoding="UTF-8"?>\n\
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"\n\
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n\
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"\n\
         version="5.0">\n\
    <filter>\n\
        <filter-name>CorsFilter</filter-name>\n\
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>\n\
        <init-param>\n\
            <param-name>cors.allowed.origins</param-name>\n\
            <param-value>*</param-value>\n\
        </init-param>\n\
        <init-param>\n\
            <param-name>cors.allowed.methods</param-name>\n\
            <param-value>GET,POST,HEAD,OPTIONS,PUT,DELETE</param-value>\n\
        </init-param>\n\
    </filter>\n\
    <filter-mapping>\n\
        <filter-name>CorsFilter</filter-name>\n\
        <url-pattern>/*</url-pattern>\n\
    </filter-mapping>\n\
</web-app>' > /usr/local/tomcat/conf/web.xml

# Copy the WAR files from builder stage
COPY --from=builder /app/EXA/target/exa.war /usr/local/tomcat/webapps/exa.war
COPY --from=builder /app/StudIP/target/studip.war /usr/local/tomcat/webapps/studip.war
COPY --from=builder /app/User/target/user.war /usr/local/tomcat/webapps/user.war

# Copy the flush.xml file
COPY --from=builder /root/xmw_data/flush.xml /root/xmw_data/flush.xml

EXPOSE 8080

VOLUME ["/root/xmw_data"]

CMD ["catalina.sh", "run"]