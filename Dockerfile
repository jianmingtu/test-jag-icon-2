##############################################################################################
#### Stage where the maven dependencies are cached                                         ###
##############################################################################################
FROM maven:3.8-eclipse-temurin-17 as dependencies-cache

ARG MVN_PROFILE

WORKDIR /build

## for the lack at a COPY --patern */pom.xml, we have to declare all the pom files manually
COPY ./pom.xml pom.xml
COPY ./bom/icon2-starters-bom/pom.xml bom/icon2-starters-bom/pom.xml
COPY ./AutomatedTests/pom.xml AutomatedTests/pom.xml
COPY ./jag-icon2-biometrics-application/pom.xml jag-icon2-biometrics-application/pom.xml
COPY ./icon2-hsr-models/pom.xml icon2-hsr-models/pom.xml
COPY ./jag-icon2-hsr-application/pom.xml jag-icon2-hsr-application/pom.xml
COPY ./jag-icon2-auth-application/pom.xml jag-icon2-auth-application/pom.xml
COPY ./icon2-code-coverage/pom.xml icon2-code-coverage/pom.xml
COPY ./icon2-common-models/pom.xml icon2-common-models/pom.xml
COPY ./jag-icon2-common-application/pom.xml jag-icon2-common-application/pom.xml
COPY ./jag-icon2-myfiles-application/pom.xml jag-icon2-myfiles-application/pom.xml

RUN  mvn dependency:go-offline \
    -P${MVN_PROFILE} \
    -DskipTests \
    --no-transfer-progress \
    --batch-mode \
    --fail-never

##############################################################################################
#### Stage where the application is built                                                  ###
##############################################################################################
FROM dependencies-cache as build

ARG MVN_PROFILE

WORKDIR /build

COPY ./AutomatedTests/src AutomatedTests/src
COPY ./jag-icon2-biometrics-application/src jag-icon2-biometrics-application/src
COPY ./icon2-hsr-models/src icon2-hsr-models/src
COPY ./jag-icon2-hsr-application/src jag-icon2-hsr-application/src
COPY ./jag-icon2-auth-application/src jag-icon2-auth-application/src
COPY ./icon2-code-coverage/lombok.config icon2-code-coverage/lombok.config
COPY ./icon2-common-models/src icon2-common-models/src
COPY ./jag-icon2-common-application/src jag-icon2-common-application/src
COPY ./jag-icon2-myfiles-application/src jag-icon2-myfiles-application/src


RUN  mvn clean package \
     -P${MVN_PROFILE} \
     -DskipTests \
     --no-transfer-progress \
     --batch-mode

##############################################################################################
#### Stage where Docker is running a java process to run a service built in previous stage ###
##############################################################################################
FROM eclipse-temurin:17-jre-jammy

ARG MVN_PROFILE

COPY --from=build /build/${MVN_PROFILE}/target/${MVN_PROFILE}*.jar /app/application.jar

ENTRYPOINT ["java", "-jar","/application.jar"]
