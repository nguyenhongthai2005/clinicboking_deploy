FROM maven:3-openjdk-22 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:22-jdk-slim
WORKDIR /app

COPY --from=build /app/target/clinic-booking-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.war"]
