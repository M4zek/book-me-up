FROM maven:3.9.6-eclipse-temurin-21 AS build

COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package -Dtest=BackendApplicationTests#!contextLoads

FROM eclipse-temurin:21-jdk

COPY --from=build /home/app/target/api.jar /usr/local/lib/api.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/api.jar"]