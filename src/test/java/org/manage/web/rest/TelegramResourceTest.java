package org.manage.web.rest;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import liquibase.Liquibase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.TestUtil;
import org.manage.domain.Member;
import org.manage.domain.User;
import org.manage.web.rest.vm.ManagedUserVM;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TelegramResourceTest {

    @Inject
    LiquibaseFactory liquibaseFactory;

    @BeforeEach
    public void databaseFixture() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            liquibase.dropAll();
            liquibase.validate();
            liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config =
            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @Test
    public void testGenerateLink() {
        var userVM = new ManagedUserVM();
        userVM.login = "testuser";
        userVM.email = "testuser@example.com";
        userVM.password = "password";
        userVM.firstName = "Test";
        userVM.lastName = "User";

        // Register
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(userVM)
            .post("/api/register")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());

        // Activate
        activateUser(userVM.login);

        String token = TestUtil.getToken(userVM.login, userVM.password);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post("/api/telegram/generate-link")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("link", containsString("imanagestuff_bot"))
            .body("link", containsString("start="))
            .body("expiresAt", notNullValue());
    }

    @Test
    public void testGenerateLinkUserWithoutMember() {
        var userVM = new ManagedUserVM();
        userVM.login = "nomember";
        userVM.email = "nomember@example.com";
        userVM.password = "password";
        userVM.firstName = "No";
        userVM.lastName = "Member";

        // Register
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(userVM)
            .post("/api/register")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());

        // Note: registerUser in UserService creates a Member, so we need to delete it to test 404
        deleteMember(userVM.login);
        activateUser(userVM.login);

        String token = TestUtil.getToken(userVM.login, userVM.password);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post("/api/telegram/generate-link")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Transactional
    public void deleteMember(String login) {
        Member.delete("login", login);
    }

    @Transactional
    public void activateUser(String login) {
        User.findOneByLogin(login).ifPresent(u -> {
            u.activated = true;
            u.persist();
        });
    }

    @Test
    public void testGenerateLinkUnauthorized() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post("/api/telegram/generate-link")
            .then()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
