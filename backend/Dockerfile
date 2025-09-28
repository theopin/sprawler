FROM tomcat:10-jdk21

# Maintainer information
LABEL maintainer="theopin"

# Expose the default Tomcat port
EXPOSE 8080

# Copy your war file into the webapps directory of Tomcat
COPY target/*.war /usr/local/tomcat/webapps/sprawler.war

# Start Tomcat when the container launches
CMD ["catalina.sh", "run"]