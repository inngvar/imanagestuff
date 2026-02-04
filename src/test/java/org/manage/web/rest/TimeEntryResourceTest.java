package org.manage.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import org.manage.TestUtil;
import org.manage.service.dto.TimeEntryDTO;
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
    import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@QuarkusTest
public class TimeEntryResourceTest {

    private static final TypeRef<TimeEntryDTO> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<TimeEntryDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";


    String adminToken;

    TimeEntryDTO timeEntryDTO;

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
    public static TimeEntryDTO createEntity() {
        var timeEntryDTO = new TimeEntryDTO();
        timeEntryDTO.duration = DEFAULT_DURATION;
        timeEntryDTO.date = DEFAULT_DATE;
        timeEntryDTO.shortDescription = DEFAULT_SHORT_DESCRIPTION;
        timeEntryDTO.description = DEFAULT_DESCRIPTION;
        return timeEntryDTO;
    }

    @BeforeEach
    public void initTest() {
        timeEntryDTO = createEntity();
        // Create a member for the TimeEntry
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
        timeEntryDTO.memberId = createdMember.id;

        // Create a project for the TimeEntry
        var projectDTO = ProjectResourceTest.createEntity();
        var createdProject = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(projectDTO)
            .when()
            .post("/api/projects")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ProjectResourceTest.ENTITY_TYPE);
        timeEntryDTO.projectId = createdProject.id;
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
        timeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testTimeEntryDTO = timeEntryDTOList.stream().filter(it -> timeEntryDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeEntryDTO.duration).isEqualTo(DEFAULT_DURATION);
        assertThat(testTimeEntryDTO.date).isEqualTo(DEFAULT_DATE);
        assertThat(testTimeEntryDTO.shortDescription).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testTimeEntryDTO.description).isEqualTo(DEFAULT_DESCRIPTION);
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
        timeEntryDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeCreate);
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
        timeEntryDTO.duration = null;

        // Create the TimeEntry, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkDateIsRequired() throws Exception {
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
        timeEntryDTO.date = null;

        // Create the TimeEntry, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
            .when()
            .post("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateTimeEntry() {
        // Initialize the database
        timeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
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
        var updatedTimeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/time-entries/{id}", timeEntryDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the timeEntry
        updatedTimeEntryDTO.duration = UPDATED_DURATION;
        updatedTimeEntryDTO.date = UPDATED_DATE;
        updatedTimeEntryDTO.shortDescription = UPDATED_SHORT_DESCRIPTION;
        updatedTimeEntryDTO.description = UPDATED_DESCRIPTION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedTimeEntryDTO)
            .when()
            .put("/api/time-entries")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeUpdate);
        var testTimeEntryDTO = timeEntryDTOList.stream().filter(it -> updatedTimeEntryDTO.id.equals(it.id)).findFirst().get();
        assertThat(testTimeEntryDTO.duration).isEqualTo(UPDATED_DURATION);
        assertThat(testTimeEntryDTO.date).isEqualTo(UPDATED_DATE);
        assertThat(testTimeEntryDTO.shortDescription).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testTimeEntryDTO.description).isEqualTo(UPDATED_DESCRIPTION);
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
            .body(timeEntryDTO)
            .when()
            .put("/api/time-entries")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the TimeEntry in the database
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteTimeEntry() {
        // Initialize the database
        timeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
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
            .delete("/api/time-entries/{id}", timeEntryDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var timeEntryDTOList = given()
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

        assertThat(timeEntryDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllTimeEntries() {
        // Initialize the database
        timeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
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
            .body("id", hasItem(timeEntryDTO.id.intValue()))
            .body("duration", hasItem(DEFAULT_DURATION.toString()))
            .body("date", hasItem(TestUtil.formatLocalDate(DEFAULT_DATE)))
            .body("shortDescription", hasItem(DEFAULT_SHORT_DESCRIPTION))
            .body("description", hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getTimeEntry() {
        // Initialize the database
        timeEntryDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(timeEntryDTO)
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
                .get("/api/time-entries/{id}", timeEntryDTO.id)
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
            .get("/api/time-entries/{id}", timeEntryDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(timeEntryDTO.id.intValue()))
            .body("duration", is(DEFAULT_DURATION.toString()))
            .body("date", is(TestUtil.formatLocalDate(DEFAULT_DATE)))
            .body("shortDescription", is(DEFAULT_SHORT_DESCRIPTION))
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
