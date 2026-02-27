package org.manage.web.rest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import org.manage.TestUtil;
import org.manage.domain.Authority;
import org.manage.domain.User;
import org.manage.security.AuthoritiesConstants;
import org.manage.service.dto.UserDTO;
import org.manage.service.mapper.UserMapper;
import org.manage.web.rest.vm.ManagedUserVM;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import java.time.Instant;
import java.util.Set;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;

import liquibase.Liquibase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceTest {
    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "jhipster";

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passjhipster";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    String adminToken;

    ManagedUserVM managedUserVM;

    @Inject
    LiquibaseFactory liquibaseFactory;

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config =
            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @BeforeEach
    public void authenticateAdmin() {
        this.adminToken = TestUtil.getAdminToken();
    }

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

    @BeforeEach
    public void initTest() {
        managedUserVM = new ManagedUserVM();
        managedUserVM.login = DEFAULT_LOGIN;
        managedUserVM.password = DEFAULT_PASSWORD;
        managedUserVM.firstName = DEFAULT_FIRSTNAME;
        managedUserVM.lastName = DEFAULT_LASTNAME;
        managedUserVM.email = DEFAULT_EMAIL;
        managedUserVM.activated = true;
        managedUserVM.imageUrl = DEFAULT_IMAGEURL;
        managedUserVM.langKey = DEFAULT_LANGKEY;
        managedUserVM.authorities = Set.of(AuthoritiesConstants.USER);
    }

    @Test
    public void createUser() {
        // Create the User
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var testUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);
        // Validate the User in the database
        assertThat(testUser.login).isEqualTo(DEFAULT_LOGIN);
        assertThat(testUser.firstName).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testUser.lastName).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testUser.email).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.imageUrl).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(testUser.langKey).isEqualTo(DEFAULT_LANGKEY);
    }

    @Test
    public void createUserWithExistingId() {
        managedUserVM.id = 1L;
        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Transactional
    public void createUserWithExistingLogin() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var otherManagedUserVM = new ManagedUserVM();
        otherManagedUserVM.login = DEFAULT_LOGIN; // this login should already be used
        otherManagedUserVM.password = DEFAULT_PASSWORD;
        otherManagedUserVM.firstName = DEFAULT_FIRSTNAME;
        otherManagedUserVM.lastName = DEFAULT_LASTNAME;
        otherManagedUserVM.email = "anothermail@localhost";
        otherManagedUserVM.activated = true;
        otherManagedUserVM.imageUrl = DEFAULT_IMAGEURL;
        otherManagedUserVM.langKey = DEFAULT_LANGKEY;
        otherManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        // Create the User
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(otherManagedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void createUserWithExistingEmail() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var otherManagedUserVM = new ManagedUserVM();
        otherManagedUserVM.login = "anotherlogin";
        otherManagedUserVM.password = DEFAULT_PASSWORD;
        otherManagedUserVM.firstName = DEFAULT_FIRSTNAME;
        otherManagedUserVM.lastName = DEFAULT_LASTNAME;
        otherManagedUserVM.email = DEFAULT_EMAIL; // this email should already be used
        otherManagedUserVM.activated = true;
        otherManagedUserVM.imageUrl = DEFAULT_IMAGEURL;
        otherManagedUserVM.langKey = DEFAULT_LANGKEY;
        otherManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        // Create the User
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void getAllUsers() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Get all the users
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/users?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("login", hasItem(DEFAULT_LOGIN))
            .body("firstName", hasItem(DEFAULT_FIRSTNAME))
            .body("lastName", hasItem(DEFAULT_LASTNAME))
            .body("email", hasItem(DEFAULT_EMAIL))
            .body("imageUrl", hasItem(DEFAULT_IMAGEURL))
            .body("langKey", hasItem(DEFAULT_LANGKEY))
            .body("login", hasItem(DEFAULT_LOGIN));
    }

    @Test
    public void getUser() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Get the user
        get("/api/users/{login}", managedUserVM.login)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("login", is(managedUserVM.login))
            .body("firstName", is(DEFAULT_FIRSTNAME))
            .body("lastName", is(DEFAULT_LASTNAME))
            .body("email", is(DEFAULT_EMAIL))
            .body("imageUrl", is(DEFAULT_IMAGEURL))
            .body("langKey", is(DEFAULT_LANGKEY))
            .body("login", is(DEFAULT_LOGIN));
    }

    @Test
    public void getNonExistingUser() {
        given().accept(APPLICATION_JSON).when().get("/api/users/unknown").then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    public void updateUser() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Update the user
        var updatedUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);

        ManagedUserVM updatedManagedUserVM = new ManagedUserVM();
        updatedManagedUserVM.id = updatedUser.id;
        updatedManagedUserVM.login = updatedUser.login;
        updatedManagedUserVM.password = UPDATED_PASSWORD;
        updatedManagedUserVM.firstName = UPDATED_FIRSTNAME;
        updatedManagedUserVM.lastName = UPDATED_LASTNAME;
        updatedManagedUserVM.email = UPDATED_EMAIL;
        updatedManagedUserVM.activated = updatedUser.activated;
        updatedManagedUserVM.imageUrl = UPDATED_IMAGEURL;
        updatedManagedUserVM.langKey = UPDATED_LANGKEY;
        updatedManagedUserVM.createdBy = updatedUser.createdBy;
        updatedManagedUserVM.createdDate = updatedUser.createdDate;
        updatedManagedUserVM.lastModifiedBy = updatedUser.lastModifiedBy;
        updatedManagedUserVM.lastModifiedDate = updatedUser.lastModifiedDate;
        updatedManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        // Update the User
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedManagedUserVM)
            .when()
            .put("/api/users")
            .then()
            .statusCode(OK.getStatusCode());

        User testUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);
        assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.email).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL);
        assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY);
    }

    @Test
    public void updateUserLogin() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Update the user
        var updatedUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);

        ManagedUserVM updatedManagedUserVM = new ManagedUserVM();
        updatedManagedUserVM.id = updatedUser.id;
        updatedManagedUserVM.login = UPDATED_LOGIN;
        updatedManagedUserVM.password = UPDATED_PASSWORD;
        updatedManagedUserVM.firstName = UPDATED_FIRSTNAME;
        updatedManagedUserVM.lastName = UPDATED_LASTNAME;
        updatedManagedUserVM.email = UPDATED_EMAIL;
        updatedManagedUserVM.activated = updatedUser.activated;
        updatedManagedUserVM.imageUrl = UPDATED_IMAGEURL;
        updatedManagedUserVM.langKey = UPDATED_LANGKEY;
        updatedManagedUserVM.createdBy = updatedUser.createdBy;
        updatedManagedUserVM.createdDate = updatedUser.createdDate;
        updatedManagedUserVM.lastModifiedBy = updatedUser.lastModifiedBy;
        updatedManagedUserVM.lastModifiedDate = updatedUser.lastModifiedDate;
        updatedManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        // Update the User
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedManagedUserVM)
            .when()
            .put("/api/users")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the User in the database
        var testUser = get("/api/users/{login}", updatedManagedUserVM.login).then().extract().body().as(User.class);
        assertThat(testUser.login).isEqualTo(UPDATED_LOGIN);
        assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.email).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL);
        assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    public void updateUserExistingEmail() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var otherManagedUserVM = new ManagedUserVM();
        otherManagedUserVM.login = "jhipster";
        otherManagedUserVM.password = RandomStringUtils.random(60);
        otherManagedUserVM.activated = true;
        otherManagedUserVM.email = "jhipster@localhost";
        otherManagedUserVM.firstName = "java";
        otherManagedUserVM.lastName = "hipster";
        otherManagedUserVM.imageUrl = "";
        otherManagedUserVM.langKey = "en";

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(otherManagedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Update the user
        var updatedUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);

        var updatedManagedUserVM = new ManagedUserVM();
        updatedManagedUserVM.id = updatedUser.id;
        updatedManagedUserVM.login = updatedUser.login;
        updatedManagedUserVM.password = updatedUser.password;
        updatedManagedUserVM.firstName = updatedUser.firstName;
        updatedManagedUserVM.lastName = updatedUser.lastName;
        updatedManagedUserVM.email = otherManagedUserVM.email; // this email should already be used by anotherManagedUserVM
        updatedManagedUserVM.activated = updatedUser.activated;
        updatedManagedUserVM.imageUrl = updatedUser.imageUrl;
        updatedManagedUserVM.langKey = updatedUser.langKey;
        updatedManagedUserVM.createdBy = updatedUser.createdBy;
        updatedManagedUserVM.createdDate = updatedUser.createdDate;
        updatedManagedUserVM.lastModifiedBy = updatedUser.lastModifiedBy;
        updatedManagedUserVM.lastModifiedDate = updatedUser.lastModifiedDate;
        updatedManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedManagedUserVM)
            .when()
            .put("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void updateUserExistingLogin() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var otherManagedUserVM = new ManagedUserVM();
        otherManagedUserVM.login = "jhipster";
        otherManagedUserVM.password = RandomStringUtils.random(60);
        otherManagedUserVM.activated = true;
        otherManagedUserVM.email = "jhipster@localhost";
        otherManagedUserVM.firstName = "java";
        otherManagedUserVM.lastName = "hipster";
        otherManagedUserVM.imageUrl = "";
        otherManagedUserVM.langKey = "en";

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(otherManagedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Update the user
        User updatedUser = get("/api/users/{login}", managedUserVM.login).then().extract().body().as(User.class);

        var updatedManagedUserVM = new ManagedUserVM();
        updatedManagedUserVM.id = updatedUser.id;
        updatedManagedUserVM.login = otherManagedUserVM.login; // this login should already be used by anotherManagedUserVM
        updatedManagedUserVM.password = updatedUser.password;
        updatedManagedUserVM.firstName = updatedUser.firstName;
        updatedManagedUserVM.lastName = updatedUser.lastName;
        updatedManagedUserVM.email = updatedUser.email;
        updatedManagedUserVM.activated = updatedUser.activated;
        updatedManagedUserVM.imageUrl = updatedUser.imageUrl;
        updatedManagedUserVM.langKey = updatedUser.langKey;
        updatedManagedUserVM.createdBy = updatedUser.createdBy;
        updatedManagedUserVM.createdDate = updatedUser.createdDate;
        updatedManagedUserVM.lastModifiedBy = updatedUser.lastModifiedBy;
        updatedManagedUserVM.lastModifiedDate = updatedUser.lastModifiedDate;
        updatedManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedManagedUserVM)
            .when()
            .put("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testCreateUserDuplicateEmailReturns400WithHeaders() {
        var firstUser = new ManagedUserVM();
        firstUser.login = "create-user-dup-email";
        firstUser.password = DEFAULT_PASSWORD;
        firstUser.firstName = DEFAULT_FIRSTNAME;
        firstUser.lastName = DEFAULT_LASTNAME;
        firstUser.email = "create-user-dup-email@example.com";
        firstUser.activated = true;
        firstUser.imageUrl = DEFAULT_IMAGEURL;
        firstUser.langKey = DEFAULT_LANGKEY;
        firstUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(firstUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var secondUser = new ManagedUserVM();
        secondUser.login = "create-user-dup-email-2";
        secondUser.password = DEFAULT_PASSWORD;
        secondUser.firstName = DEFAULT_FIRSTNAME;
        secondUser.lastName = DEFAULT_LASTNAME;
        secondUser.email = firstUser.email;
        secondUser.activated = true;
        secondUser.imageUrl = DEFAULT_IMAGEURL;
        secondUser.langKey = DEFAULT_LANGKEY;
        secondUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(secondUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .header("message", "error.emailexists");
    }

    @Test
    public void testCreateUserDuplicateLoginReturns400WithHeaders() {
        var firstUser = new ManagedUserVM();
        firstUser.login = "create-user-dup-login";
        firstUser.password = DEFAULT_PASSWORD;
        firstUser.firstName = DEFAULT_FIRSTNAME;
        firstUser.lastName = DEFAULT_LASTNAME;
        firstUser.email = "create-user-dup-login@example.com";
        firstUser.activated = true;
        firstUser.imageUrl = DEFAULT_IMAGEURL;
        firstUser.langKey = DEFAULT_LANGKEY;
        firstUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(firstUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var secondUser = new ManagedUserVM();
        secondUser.login = firstUser.login;
        secondUser.password = DEFAULT_PASSWORD;
        secondUser.firstName = DEFAULT_FIRSTNAME;
        secondUser.lastName = DEFAULT_LASTNAME;
        secondUser.email = "another-email@example.com";
        secondUser.activated = true;
        secondUser.imageUrl = DEFAULT_IMAGEURL;
        secondUser.langKey = DEFAULT_LANGKEY;
        secondUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(secondUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .header("message", "error.userexists");
    }

    @Test
    public void testUpdateUserDuplicateEmailReturns400WithHeaders() {
        var firstUser = new ManagedUserVM();
        firstUser.login = "update-user-dup-email";
        firstUser.password = DEFAULT_PASSWORD;
        firstUser.firstName = DEFAULT_FIRSTNAME;
        firstUser.lastName = DEFAULT_LASTNAME;
        firstUser.email = "update-user-dup-email@example.com";
        firstUser.activated = true;
        firstUser.imageUrl = DEFAULT_IMAGEURL;
        firstUser.langKey = DEFAULT_LANGKEY;
        firstUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(firstUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var secondUser = new ManagedUserVM();
        secondUser.login = "update-user-dup-email-2";
        secondUser.password = DEFAULT_PASSWORD;
        secondUser.firstName = DEFAULT_FIRSTNAME;
        secondUser.lastName = DEFAULT_LASTNAME;
        secondUser.email = "update-user-dup-email-2@example.com";
        secondUser.activated = true;
        secondUser.imageUrl = DEFAULT_IMAGEURL;
        secondUser.langKey = DEFAULT_LANGKEY;
        secondUser.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(secondUser)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        var updatedFirstUser = get("/api/users/{login}", firstUser.login).then().extract().body().as(User.class);

        var updatedManagedUserVM = new ManagedUserVM();
        updatedManagedUserVM.id = updatedFirstUser.id;
        updatedManagedUserVM.login = updatedFirstUser.login;
        updatedManagedUserVM.password = updatedFirstUser.password;
        updatedManagedUserVM.firstName = updatedFirstUser.firstName;
        updatedManagedUserVM.lastName = updatedFirstUser.lastName;
        updatedManagedUserVM.email = secondUser.email;
        updatedManagedUserVM.activated = updatedFirstUser.activated;
        updatedManagedUserVM.imageUrl = updatedFirstUser.imageUrl;
        updatedManagedUserVM.langKey = updatedFirstUser.langKey;
        updatedManagedUserVM.createdBy = updatedFirstUser.createdBy;
        updatedManagedUserVM.createdDate = updatedFirstUser.createdDate;
        updatedManagedUserVM.lastModifiedBy = updatedFirstUser.lastModifiedBy;
        updatedManagedUserVM.lastModifiedDate = updatedFirstUser.lastModifiedDate;
        updatedManagedUserVM.authorities = Set.of(AuthoritiesConstants.USER);

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedManagedUserVM)
            .when()
            .put("/api/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .header("message", "error.emailexists");
    }

    @Test
    public void deleteUser() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(managedUserVM)
            .when()
            .post("/api/users")
            .then()
            .statusCode(CREATED.getStatusCode());

        // Delete the user
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/users/{login}", managedUserVM.login)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the user has been removed database
        get("/api/users/{login}", managedUserVM.login).then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    public void getAllAuthorities() {
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/users/authorities")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("$", hasSize(greaterThan(0)))
            .body("$", hasItems(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testUserDTOtoUser() {
        var userMapper = new UserMapper();
        var userDTO = new UserDTO();
        userDTO.id = 1L;
        userDTO.login = DEFAULT_LOGIN;
        userDTO.firstName = DEFAULT_FIRSTNAME;
        userDTO.lastName = DEFAULT_LASTNAME;
        userDTO.email = DEFAULT_EMAIL;
        userDTO.activated = true;
        userDTO.imageUrl = DEFAULT_IMAGEURL;
        userDTO.langKey = DEFAULT_LANGKEY;
        userDTO.createdBy = DEFAULT_LOGIN;
        userDTO.lastModifiedBy = DEFAULT_LOGIN;
        userDTO.authorities = Set.of(AuthoritiesConstants.USER);

        User user = userMapper.userDTOToUser(userDTO);
        assertThat(user.id).isEqualTo(1L);
        assertThat(user.login).isEqualTo(DEFAULT_LOGIN);
        assertThat(user.firstName).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(user.lastName).isEqualTo(DEFAULT_LASTNAME);
        assertThat(user.email).isEqualTo(DEFAULT_EMAIL);
        assertThat(user.activated).isEqualTo(true);
        assertThat(user.imageUrl).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(user.langKey).isEqualTo(DEFAULT_LANGKEY);
        assertThat(user.createdBy).isBlank();
        assertThat(user.createdDate).isNotNull();
        assertThat(user.lastModifiedBy).isBlank();
        assertThat(user.lastModifiedDate).isNotNull();
        assertThat(user.authorities).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }

    @Test
    public void testUserToUserDTO() {
        var userMapper = new UserMapper();
        var user = new User();
        user.id = 1L;
        user.login = DEFAULT_LOGIN;
        user.password = DEFAULT_PASSWORD;
        user.firstName = DEFAULT_FIRSTNAME;
        user.lastName = DEFAULT_LASTNAME;
        user.email = DEFAULT_EMAIL;
        user.activated = true;
        user.imageUrl = DEFAULT_IMAGEURL;
        user.langKey = DEFAULT_LANGKEY;
        user.createdBy = DEFAULT_LOGIN;
        user.createdDate = Instant.now();
        user.lastModifiedBy = DEFAULT_LOGIN;
        user.lastModifiedDate = Instant.now();
        var authority = new Authority();
        authority.name = AuthoritiesConstants.USER;
        user.authorities = Set.of(authority);

        UserDTO userDTO = userMapper.userToUserDTO(user);

        assertThat(userDTO.id).isEqualTo(1L);
        assertThat(userDTO.login).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.firstName).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userDTO.lastName).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userDTO.email).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.activated).isEqualTo(true);
        assertThat(userDTO.imageUrl).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(userDTO.langKey).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userDTO.createdBy).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.createdDate).isEqualTo(user.createdDate);
        assertThat(userDTO.lastModifiedBy).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.lastModifiedDate).isEqualTo(user.lastModifiedDate);
        assertThat(userDTO.authorities).containsExactly(AuthoritiesConstants.USER);
        assertThat(userDTO.toString()).isNotNull();
    }
}
