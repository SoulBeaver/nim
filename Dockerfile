FROM openjdk:13-alpine

WORKDIR /var/nim

ADD build/libs/nim.jar /var/nim/nim.jar

EXPOSE 8080 8080

ENTRYPOINT ["java", "-jar", "nim.jar"]