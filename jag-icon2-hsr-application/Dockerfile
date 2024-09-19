FROM eclipse-temurin:17-jre-alpine

RUN apk upgrade expat  # Fix for CVE-2022-43680

COPY ./target/jag-icon2-hsr-application.jar jag-icon2-hsr-application.jar

ENTRYPOINT ["java", "-jar","/jag-icon2-hsr-application.jar"]
