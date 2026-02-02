package org.manage.web.rest;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.TestUtil;
import org.manage.service.dto.TimeLogDTO;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TimeLogResourceTest {

    private static final TypeRef<TimeLogDTO> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<TimeLogDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_CHECK_IN = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CHECK_IN = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);

    private static final ZonedDateTime DEFAULT_CHECK_OUT = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CHECK_OUT = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);


    String adminToken;

    TimeLogDTO timeLogDTO;

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
    public static TimeLogDTO createEntity() {
        var timeLogDTO = new TimeLogDTO();
        timeLogDTO.date = DEFAULT_DATE;
        timeLogDTO.checkIn = DEFAULT_CHECK_IN;
        timeLogDTO.checkOut = DEFAULT_CHECK_OUT;
        return timeLogDTO;
    }

    @BeforeEach
    public void initTest() {
        timeLogDTO = createEntity();
        // Create a member for the TimeLog
        var memberDTO = MemberResourceTest.createEntity();
        var createdMember = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(memberDTO)
            .when()
            .post("/api/members")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(MemberResourceTest.ENTITY_TYPE);
        timeLogDTO.memberId = createdMember.id;
    }

    @Test
    public void createTimeLog() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeLog
        timeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the TimeLog in the database
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testTimeLogDTO = timeLogDTOList.stream().filter(it -> timeLogDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeLogDTO.date).isEqualTo(DEFAULT_DATE);
        assertThat(testTimeLogDTO.checkIn).isEqualTo(DEFAULT_CHECK_IN);
        assertThat(testTimeLogDTO.checkOut).isEqualTo(DEFAULT_CHECK_OUT);
    }

    @Test
    public void createTimeLogWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeLog with an existing ID
        timeLogDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeLog in the database
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkDateIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        timeLogDTO.date = null;

        // Create the TimeLog, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeLog in the database
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateTimeLog() {
        // Initialize the database
        timeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the timeLog
        var updatedTimeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs/{id}", timeLogDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the timeLog
        updatedTimeLogDTO.date = UPDATED_DATE;
        updatedTimeLogDTO.checkIn = UPDATED_CHECK_IN;
        updatedTimeLogDTO.checkOut = UPDATED_CHECK_OUT;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedTimeLogDTO)
            .when()
            .put("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the TimeLog in the database
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeUpdate);
        var testTimeLogDTO = timeLogDTOList.stream().filter(it -> updatedTimeLogDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeLogDTO.date).isEqualTo(UPDATED_DATE);
        assertThat(testTimeLogDTO.checkIn).isEqualTo(UPDATED_CHECK_IN);
        assertThat(testTimeLogDTO.checkOut).isEqualTo(UPDATED_CHECK_OUT);
    }

    @Test
    public void updateNonExistingTimeLog() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
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
            .body(timeLogDTO)
            .when()
            .put("/api/time-logs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeLog in the database
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteTimeLog() {
        // Initialize the database
        timeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the timeLog
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/time-logs/{id}", timeLogDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var timeLogDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeLogDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllTimeLogs() {
        // Initialize the database
        timeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the timeLogList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(timeLogDTO.id.intValue()))
            .body("date", hasItem(TestUtil.formatLocalDate(DEFAULT_DATE)))
            .body("checkIn", hasItem(TestUtil.formatZonedDateTime(DEFAULT_CHECK_IN)))
            .body("checkOut", hasItem(TestUtil.formatZonedDateTime(DEFAULT_CHECK_OUT)));
    }

    @Test
    public void getTimeLog() {
        // Initialize the database
        timeLogDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeLogDTO)
            .when()
            .post("/api/time-logs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the timeLog
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/time-logs/{id}", timeLogDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the timeLog
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs/{id}", timeLogDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(timeLogDTO.id.intValue()))
            .body("date", is(TestUtil.formatLocalDate(DEFAULT_DATE)))
            .body("checkIn", is(TestUtil.formatZonedDateTime(DEFAULT_CHECK_IN)))
            .body("checkOut", is(TestUtil.formatZonedDateTime(DEFAULT_CHECK_OUT)));
    }

    @Test
    public void getNonExistingTimeLog() {
        // Get the timeLog
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-logs/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
