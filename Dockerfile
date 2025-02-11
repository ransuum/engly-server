FROM maven:3.8.8-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

FROM amazoncorretto:21
WORKDIR /app
RUN ls
COPY --from=build /app/target/*.jar ./app.jar
RUN ls
ENTRYPOINT ["java", "-jar", "app.jar"]