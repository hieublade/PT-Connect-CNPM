FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copy pom.xml và source code
COPY pom.xml .
COPY src ./src

# Biên dịch trước mã nguồn và tải plugin Jetty để tạo cache trong Docker image
RUN mvn clean compile -DskipTests

EXPOSE 8089

CMD ["mvn", "org.eclipse.jetty.ee10:jetty-ee10-maven-plugin:run"]
