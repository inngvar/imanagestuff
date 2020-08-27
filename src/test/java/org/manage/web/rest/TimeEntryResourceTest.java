package org.manage.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import org.manage.TestUtil;
import org.manage.domain.TimeEntry;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.Duration;
    import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@QuarkusTest
public class TimeEntryResourceTest {

    private static final TypeRef<TimeEntry> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<TimeEntry>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private static final String DEFAULT_SHOT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHOT_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";


    String adminToken;

    TimeEntry timeEntry;

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
    public static TimeEntry createEntity() {
        var timeEntry = new TimeEntry();
        timeEntry.duration = DEFAULT_DURATION;
        timeEntry.timestamp = DEFAULT_TIMESTAMP;
        timeEntry.shotDescription = DEFAULT_SHOT_DESCRIPTION;
        timeEntry.description = DEFAULT_DESCRIPTION;
        return timeEntry;
    }

    @BeforeEach
    public void initTest() {
        timeEntry = createEntity();
    }

    @Test
    public void createTimeEntry() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeEntry
        timeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeCreate + 1);
        var testTimeEntry = timeEntryList.stream().filter(it -> timeEntry.id.equals(it.id)).findFirst().get();
        assertThat(testTimeEntry.duration).isEqualTo(DEFAULT_DURATION);
        assertThat(testTimeEntry.timestamp).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testTimeEntry.shotDescription).isEqualTo(DEFAULT_SHOT_DESCRIPTION);
        assertThat(testTimeEntry.description).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createTimeEntryWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the TimeEntry with an existing ID
        timeEntry.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkDurationIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        timeEntry.duration = null;

        // Create the TimeEntry, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkTimestampIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        timeEntry.timestamp = null;

        // Create the TimeEntry, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateTimeEntry() {
        // Initialize the database
        timeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the timeEntry
        var updatedTimeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries/{id}", timeEntry.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the timeEntry
        updatedTimeEntry.duration = UPDATED_DURATION;
        updatedTimeEntry.timestamp = UPDATED_TIMESTAMP;
        updatedTimeEntry.shotDescription = UPDATED_SHOT_DESCRIPTION;
        updatedTimeEntry.description = UPDATED_DESCRIPTION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedTimeEntry)
            .when()
            .put("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
        var testTimeEntry = timeEntryList.stream().filter(it -> updatedTimeEntry.id.equals(it.id)).findFirst().get();
        assertThat(testTimeEntry.duration).isEqualTo(UPDATED_DURATION);
        assertThat(testTimeEntry.timestamp).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testTimeEntry.shotDescription).isEqualTo(UPDATED_SHOT_DESCRIPTION);
        assertThat(testTimeEntry.description).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void updateNonExistingTimeEntry() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
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
            .body(timeEntry)
            .when()
            .put("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteTimeEntry() {
        // Initialize the database
        timeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the timeEntry
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/time-entries/{id}", timeEntry.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var timeEntryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(timeEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllTimeEntries() {
        // Initialize the database
        timeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the timeEntryList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(timeEntry.id.intValue()))
            .body("duration", hasItem(DEFAULT_DURATION.toString()))            .body("timestamp", hasItem(TestUtil.formatDateTime(DEFAULT_TIMESTAMP)))            .body("shotDescription", hasItem(DEFAULT_SHOT_DESCRIPTION))            .body("description", hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getTimeEntry() {
        // Initialize the database
        timeEntry = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntry)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the timeEntry
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/time-entries/{id}", timeEntry.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the timeEntry
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries/{id}", timeEntry.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(timeEntry.id.intValue()))
            
                .body("duration", is(DEFAULT_DURATION.toString()))
                .body("timestamp", is(TestUtil.formatDateTime(DEFAULT_TIMESTAMP)))
                .body("shotDescription", is(DEFAULT_SHOT_DESCRIPTION))
                .body("description", is(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getNonExistingTimeEntry() {
        // Get the timeEntry
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
