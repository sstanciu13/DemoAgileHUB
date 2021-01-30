package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import requests.CreateUser.CreateUserRequest;
import responses.createuser.CreateUserResponse;
import responses.getuser.GetUserResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestClass3POJO extends TestBase{

    @Test
    public void getUserById(){
        logger.info("This test is finding a specific user, by ID.");

        int testUserId = 2;

        logger.debug("Sending request to get the user with id: " + testUserId);
        Response response =
                            given()
                                    .spec(requestSpecification)
                                    .baseUri(baseUrl)
                                    .pathParam("id", Integer.toString(testUserId))
                            .when()
                                    .get(resourceUsers + "/{id}")
                            .then()
                                    .statusCode(200)
                                    .extract().response();

        GetUserResponse getUserResponse = response.as(GetUserResponse.class);

        assertThat(getUserResponse.data.id, equalTo(testUserId));
        assertThat(getUserResponse.data.email, equalTo("janet.weaver@reqres.in"));
        assertThat(getUserResponse.data.first_name, equalTo("Janet"));
        assertThat(getUserResponse.data.last_name, equalTo("Weaver"));
        assertThat(getUserResponse.support.url, equalTo("https://reqres.in/#support-heading"));
    }

    @Test(groups = {"DeleteUserNeeded"})
    public void userCreateReadDelete(ITestContext iTestContext){
        logger.info("This test is creating a new user, is checking it, then is deleting it.");

        String testUserName = "SilviuDemoAgileHUB";
        String testUserJob = "TestAutomation";
        int expectedStatusCode = 201;

        CreateUserRequest createUserRequest =
                new CreateUserRequest().setName(testUserName).setJob(testUserJob);

        logger.debug("Creating a new user.");
        Response response =
                given()
                        .spec(requestSpecification)
                        .contentType(ContentType.JSON)
                        .baseUri(baseUrl)
                        .body(createUserRequest)
                .when()
                        .post(resourceUsers)
                .then()
                        .statusCode(expectedStatusCode)
                        .extract().response();

        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);

        assertThat("Wrong user name created, expected: " + testUserName + " but found " + createUserResponse.name,
                createUserResponse.name, equalTo(testUserName));

        assertThat("Wrong job created, expected: " + testUserJob + " but found " + createUserResponse.job,
                createUserResponse.job, equalTo(testUserJob));

        assertThat("Id returned in response was null, was not supposed to be null",
                createUserResponse.id, is(notNullValue()));

        assertThat("createdAt returned in response was null, was not supposed to be null",
                createUserResponse.createdAt, is(notNullValue()));

        iTestContext.setAttribute("userToDelete", createUserResponse.id);
    }
}
