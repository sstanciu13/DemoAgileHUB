package tests;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestClass2Improvements extends TestBase{

    @Test
    public void getUserById(){
        logger.info("This test is finding a specific user, by ID.");

        int testUserId = 2;

        logger.debug("Sending request to get the user with id: " + testUserId);
        given()
                .spec(requestSpecification)
                .baseUri(baseUrl)
                .pathParam("id", Integer.toString(testUserId))
        .when()
                .get(resourceUsers + "/{id}")
        .then()
                .statusCode(200)
                .assertThat()
                    .body("data.id", equalTo(testUserId))
                    .body("data.email", equalTo("janet.weaver@reqres.in"))
                    .body("data.first_name", equalTo("Janet"))
                    .body("data.last_name", equalTo("Weaver"))
                    .body("support.url", equalTo("https://reqres.in/#support-heading"));
    }

    @Test (priority = -1)
    public void getUserByPage(){
        logger.info("This test is retrieving users, for a specific page.");
        logger.info("Default display is 6 users per page.");

        int pageIndexId = 2;
        int expectedNumberOfUsersPerPage = 6;

        logger.debug("Sending request to get the users by page index: " + pageIndexId);
        Response response =
                            given()
                                .filter(new RequestLoggingFilter())
                                .filter(new ResponseLoggingFilter())
                                .baseUri("https://reqres.in")
                                .queryParam("page", Integer.toString(pageIndexId))
                            .when()
                                .get("/api/users")
                            .then()
                                .statusCode(200)
                                .extract().response();

        assertThat(response.jsonPath().getInt("page"), equalTo(pageIndexId));
        assertThat(response.jsonPath().getInt("per_page"), equalTo(expectedNumberOfUsersPerPage));
        assertThat(response.jsonPath().getString("data[0].email"), allOf(containsString("michael.lawson"), containsString("@")));
        assertThat(response.jsonPath().getList("data.email", String.class), hasItems("tobias.funke@reqres.in", "lindsay.ferguson@reqres.in"));
        assertThat(response.jsonPath().getList("data.email", String.class), everyItem(allOf(containsString("@reqres.in"), containsString("."))));
        assertThat(response.jsonPath().getList("data.id", Integer.class), containsInAnyOrder(12,8,9,10,11,7));
    }

    @Test()
    public void getUserByIdDelete(){
        logger.info("This test is finding a specific user, by ID, then is deleting this user.");

        int testUserId = 2;

        logger.debug("Sending request to get the user with id: " + testUserId);
        given()
                .spec(requestSpecification)
                .baseUri(baseUrl)
                .pathParam("id", Integer.toString(testUserId))
        .when()
                .get(resourceUsers + "/{id}")
        .then()
                .statusCode(200)
                .assertThat()
                .body("data.id", equalTo(testUserId))
                .body("data.email", equalTo("janet.weaver@reqres.in"))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"))
                .body("support.url", equalTo("https://reqres.in/#support-heading"));
    }
}
