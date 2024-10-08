package com.user;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class E2eTestSupport {

    private static final DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8");

    static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>(MYSQL_IMAGE);
        MY_SQL_CONTAINER.start();
    }

    @LocalServerPort
    int port;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("storage.datasource.master.driver-class-name", MY_SQL_CONTAINER::getDriverClassName);
        registry.add("storage.datasource.master.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("storage.datasource.master.username", MY_SQL_CONTAINER::getUsername);
        registry.add("storage.datasource.master.password", MY_SQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }
}
