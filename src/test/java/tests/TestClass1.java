package tests;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestClass1 {

    @Test
    public void getUserById(){
        System.out.println("This test is finding a specific user, by ID.");

        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .baseUri("https://reqres.in")
                .pathParam("id", "2")
        .when()
                .get("/api/users" + "/{id}")
        .then()
                .statusCode(200)
                .assertThat()
                    .body("data.id", equalTo(2))
                    .body("data.email", equalTo("janet.weaver@reqres.in"))
                    .body("data.first_name", equalTo("Janet"))
                    .body("data.last_name", equalTo("Weaver"))
                    .body("support.url", equalTo("https://reqres.in/#support-heading"));
    }

    @Test
    public void getUsersByPage(){
        System.out.println("This test is retrieving users, for a specific page.");
        System.out.println("Default display is 6 users per page.");

        given()
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter())
            .baseUri("https://reqres.in")
            .queryParam("page", "2")
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .assertThat()
                .body("page", equalTo(2))
                .body("per_page", equalTo(6))
                .body("data[0].email", allOf(containsString("michael.lawson"), containsString("@")))
                .body("data.email", hasItems("tobias.funke@reqres.in", "lindsay.ferguson@reqres.in"))
                .body("data.email", everyItem(allOf(containsString("@reqres.in"), containsString("."))))
                .body("data.id", containsInAnyOrder(12,8,9,10,11,7));
    }

    @Test
    public void getUserByPageExtractResponse(){
        System.out.println("This test is retrieving users, for a specific page.");

        Response response =
                            given()
                                    .filter(new RequestLoggingFilter())
                                    .filter(new ResponseLoggingFilter())
                                    .baseUri("https://reqres.in")
                                    .queryParam("page", "2")
                            .when()
                                    .get("/api/users")
                            .then()
                                    .statusCode(200)
                                    .extract().response();

        assertThat(response.jsonPath().getInt("page"), equalTo(2));
        assertThat(response.jsonPath().getInt("per_page"), equalTo(6));
        assertThat(response.jsonPath().getString("data[0].email"), allOf(containsString("michael.lawson"), containsString("@")));
        assertThat(response.jsonPath().getList("data.email", String.class), hasItems("tobias.funke@reqres.in", "lindsay.ferguson@reqres.in"));
        assertThat(response.jsonPath().getList("data.email", String.class), everyItem(allOf(containsString("@reqres.in"), containsString("."))));
        assertThat(response.jsonPath().getList("data.id", Integer.class), containsInAnyOrder(12,8,9,10,11,7));
    }

    @Test
    public void userCreateReadDelete(){
        System.out.println("This test is creating a new user, is checking it, then is deleting it.");

        File createUserJsonFile = new File(System.getProperty("user.dir")
                + ".src.test.resources.test_data.".replace(".", System.getProperty("file.separator"))
                + "CreateUser.json");

        System.out.println("Creating a new user.");
        Response response =
                            given()
                                    .filter(new RequestLoggingFilter())
                                    .filter(new ResponseLoggingFilter())
                                    .contentType(ContentType.JSON)
                                    .baseUri("https://reqres.in")
                                    .body(createUserJsonFile)
                            .when()
                                    .post("/api/users")
                            .then()
                                    .statusCode(201)
                                    .extract().response();

        String newlyCreatedUserId = response.jsonPath().getString("id");

        System.out.println("Checking the newly created user, id: " + newlyCreatedUserId);
        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .baseUri("https://reqres.in")
                .pathParam("id", newlyCreatedUserId)
        .when()
                .get("/api/users" + "/{id}")
        .then()
                .statusCode(200)
                .assertThat()
                .body("data.id", equalTo(newlyCreatedUserId));

        System.out.println("Deleting the newly created user, id: " + newlyCreatedUserId);
        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .baseUri("https://reqres.in")
                .pathParam("id", newlyCreatedUserId)
        .when()
                .delete("/api/users" + "/{id}")
        .then()
                .statusCode(204);
    }
}
