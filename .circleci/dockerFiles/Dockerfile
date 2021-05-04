# Version 1.8
# Felix Diaz
# Scope Retail

#To use in CircleCI

FROM alpine:3.12

RUN apk add openjdk11-jre

RUN addgroup -S sirengroup && adduser -S siren -G sirengroup

RUN mkdir /home/tomcat && mkdir /appl

COPY app.jar /home/tomcat/app.jar

RUN chown siren:sirengroup /home/tomcat -R && chown siren:sirengroup /appl -R

USER siren

EXPOSE 8080

ENTRYPOINT java -jar  -Dspring.profiles.active=docker /home/tomcat/app.jar

