FROM maven:3.8.5-openjdk-26 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:26-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]