# ---- build WAR ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package

# ---- chạy bằng Tomcat 10.1 ----
FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/baitapweb.war /usr/local/tomcat/webapps/ROOT.war
COPY start.sh /start.sh
RUN chmod +x /start.sh
ENV JAVA_OPTS="-Xmx300m -Dfile.encoding=UTF-8"
EXPOSE 8080
CMD ["/start.sh"]
