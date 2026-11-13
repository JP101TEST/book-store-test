FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM scratch AS artifact
COPY --from=build /app/target/backend_aws.jar /backend_aws.jar
COPY --from=build /app/src/main/resources/application.properties application.properties
COPY --from=build /app/src/main/resources/application-sit.properties application-sit.properties
COPY --from=build /app/src/main/resources/deploy.sh deploy.sh

# docker build --target artifact --output type=local,dest=./dist .