FROM eclipse-temurin:21

WORKDIR /app

COPY build/libs/api-admin*.jar api-admin.jar

EXPOSE 8081

CMD ["java", "-jar", "api-admin.jar"]