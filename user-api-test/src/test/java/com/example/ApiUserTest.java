package com.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.Story;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Story("User CRUD tests")
public class ApiUserTest {
    @BeforeAll
    static void setup() {
        String apiBaseUrl = System.getenv("API_BASE_URL");
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "http://api:8080/api";
        }
        System.out.println("Using API Base URL: " + apiBaseUrl);

        RestAssured.baseURI = apiBaseUrl;
        RestAssured.authentication = basic("username", "password");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        System.setProperty("allure.results.directory", "target/allure-results");
    }

    private List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        Response responseAllUsers = RestAssured.get("/users");
        assertEquals(200, responseAllUsers.getStatusCode(), "GET request failed");
        JsonPath jsonPath = responseAllUsers.jsonPath();
        users = jsonPath.getList("$");
        return users;
    }

    @Test
    @Step("GET request")
    @Description("Get - non existant User ID")
    public void getUserWrongId() {
        given().
            header("Content-Type", "application/json").
        when().
            get("users/not-an-existing-user-id").
        then().
            statusCode(404);
    }

    @Test
    @Step("DELETE request")
    @Description("Delete - nonexistent User ID")
    public void deleteUserWrongId() {
        given().
            header("Content-Type", "application/json").
        when().
            delete("users/not-an-existing-user-id").
        then().
            statusCode(404);
    }
   
    @ParameterizedTest
    @Step("POST request")
    @Description("Post - invalid input")
    @MethodSource("com.example.TestData#provideInvalidRequestBodies")
    public void postRequestWithInvalidInput(String requestBody) {
        given().
            header("Content-Type", "application/json").
            body(requestBody).
        when().
            post("/users").
        then().
            statusCode(400);
    }
    
    @ParameterizedTest
    @Step("PUT request")
    @MethodSource("com.example.TestData#provideValidRequestBody")
    @Description("Put - invalid user ID")
    public void putRequestWithInvalidUserId(String requestBody) {
        given().
            header("Content-Type", "application/json").
            body(requestBody).
        when().
            put("/users/not-an-existing-user-id").
        then().
            statusCode(404);
    }
    
    @ParameterizedTest
    @Step("PUT request")
    @MethodSource("com.example.TestData#provideInvalidRequestBodies")
    @Description("Put - invalid input data")
    public void putRequestWithInvalidInputData(String putRequestBody) {
        String postRequestBody = 
            """                
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "dateOfBirth": "1985-10-01",
                    "personalIdDocument":
                    { 
                        "documentId": "AB123456",
                        "countryOfIssue": "US",
                        "validUntil": "2030-12-31"
                    }   
                }
            """;
            
        Response response =
        given().
            header("Content-Type", "application/json").
            body(postRequestBody).
        when().
            post("/users").
        then().
            statusCode(201).
        extract().
            response();

        String userIdString = response.path("id");

        given().
            header("Content-Type", "application/json").
            body(putRequestBody).
        when().
            put("users/" + userIdString).
        then().
            statusCode(400);

        given().
            header("Content-Type", "application/json").
        when().
            delete("users/" + userIdString).
        then().
            statusCode(204);
    }
   
    @ParameterizedTest
    @Step("POST request")
    @Description("Post and delete user, positive scenario")
    @MethodSource("com.example.TestData#provideValidRequestBody")
    public void postRequestWithValidInput(String requestBody) {
        List<Map<String, Object>> users = getAllUsers();
        int userCountBeforePost = users.size();
        
        Response response =
        given().
            header("Content-Type", "application/json").
            body(requestBody).
        when().
            post("/users").
        then().
            statusCode(201).
        extract().
            response();
        
        String userIdString = response.path("id");

        users = getAllUsers();
        int userCountAfterPost = users.size();
        assertEquals(userCountBeforePost + 1, userCountAfterPost, "Quantity of users failed to increase.");

        given().
            header("Content-Type", "application/json").
        when().
            get("users/" + userIdString).
        then().
            statusCode(200);

        given().
            header("Content-Type", "application/json").
        when().
            delete("users/" + userIdString).
        then().
            statusCode(204);

        users = getAllUsers();
        int userCountAfterdelete = users.size();
        assertEquals(userCountAfterPost - 1, userCountAfterdelete, "Quantity of users failed to decrease.");

        given().
            header("Content-Type", "application/json").
        when().
            get("users/" + userIdString).
        then().
            statusCode(404);
    }
}
