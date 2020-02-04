FROM java:openjdk-13

WORKDIR /var/nim

ADD build/libs/nim.jar /var/nim/nim.jar

EXPOSE 9000 9000

ENTRYPOINT ["java", "-jar", "nim.jar"]