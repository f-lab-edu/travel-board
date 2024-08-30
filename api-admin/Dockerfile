FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/api-admin*.jar api-admin.jar

EXPOSE 8080

CMD ["java", "-jar", "api-admin.jar"]