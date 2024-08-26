FROM eclipse-temurin:21

WORKDIR /app

COPY build/libs/api-user*.jar api-user.jar

EXPOSE 8080

CMD ["java", "-jar", "api-user.jar"]