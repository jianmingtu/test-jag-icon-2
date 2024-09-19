FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN chgrp -R 0 /app && \
    chmod -R g=u /app

COPY ./target/ICON2-test-runner.jar .

ENTRYPOINT ["java","-Xss2048k","-jar","ICON2-test-runner.jar"]
