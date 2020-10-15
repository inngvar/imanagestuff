package org.manage.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import org.manage.TestUtil;
import org.manage.service.dto.TimeCheckTaskDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

    import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@QuarkusTest
public class TimeCheckTaskResourceTest {

    private static final TypeRef<TimeCheckTaskDTO> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<TimeCheckTaskDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());


    String adminToken;

    TimeCheckTaskDTO timeCheckTaskDTO;

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

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeCheckTaskDTO createEntity() {
        var timeCheckTaskDTO = new TimeCheckTaskDTO();
        timeCheckTaskDTO.date = DEFAULT_DATE;
        return timeCheckTaskDTO;
    }

    @BeforeEach
    public void initTest() {
        timeCheckTaskDTO = createEntity();
    }

    @Test
    public void createTimeCheckTask() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeCheckTask
        timeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the TimeCheckTask in the database
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testTimeCheckTaskDTO = timeCheckTaskDTOList.stream().filter(it -> timeCheckTaskDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeCheckTaskDTO.date).isEqualTo(DEFAULT_DATE);
    }

    @Test
    public void createTimeCheckTaskWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeCheckTask with an existing ID
        timeCheckTaskDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeCheckTask in the database
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkDateIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        timeCheckTaskDTO.date = null;

        // Create the TimeCheckTask, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeCheckTask in the database
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateTimeCheckTask() {
        // Initialize the database
        timeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the timeCheckTask
        var updatedTimeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks/{id}", timeCheckTaskDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the timeCheckTask
        updatedTimeCheckTaskDTO.date = UPDATED_DATE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedTimeCheckTaskDTO)
            .when()
            .put("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the TimeCheckTask in the database
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeUpdate);
        var testTimeCheckTaskDTO = timeCheckTaskDTOList.stream().filter(it -> updatedTimeCheckTaskDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeCheckTaskDTO.date).isEqualTo(UPDATED_DATE);
    }

    @Test
    public void updateNonExistingTimeCheckTask() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .put("/api/time-check-tasks")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeCheckTask in the database
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteTimeCheckTask() {
        // Initialize the database
        timeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the timeCheckTask
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/time-check-tasks/{id}", timeCheckTaskDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var timeCheckTaskDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeCheckTaskDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllTimeCheckTasks() {
        // Initialize the database
        timeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the timeCheckTaskList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(timeCheckTaskDTO.id.intValue()))
            .body("date", hasItem(TestUtil.formatDateTime(DEFAULT_DATE)));
    }

    @Test
    public void getTimeCheckTask() {
        // Initialize the database
        timeCheckTaskDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeCheckTaskDTO)
            .when()
            .post("/api/time-check-tasks")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the timeCheckTask
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/time-check-tasks/{id}", timeCheckTaskDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the timeCheckTask
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks/{id}", timeCheckTaskDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(timeCheckTaskDTO.id.intValue()))
            
                .body("date", is(TestUtil.formatDateTime(DEFAULT_DATE)));
    }

    @Test
    public void getNonExistingTimeCheckTask() {
        // Get the timeCheckTask
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-check-tasks/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
