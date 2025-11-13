# Build stage
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

# copy toàn bộ source vào container
COPY . .

# build project, bỏ qua test cho nhanh
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# nếu artifact của bạn là WAR như hiện tại
COPY --from=build /app/target/clinic-booking-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.war"]
