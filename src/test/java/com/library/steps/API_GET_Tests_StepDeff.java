package com.library.steps;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.List;

public class API_GET_Tests_StepDeff {

    //    LibraryAPI_Util libraryAPIUtil = new LibraryAPI_Util();
    RequestSpecification pathParamRequest;
    RequestSpecification basicRequest;
    String librarianToken;
    Response response;
    String acceptType;
    String pathParam;
    String endpoint;
    String userType;

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        librarianToken = LibraryAPI_Util.getToken(userType);
        this.userType = userType;
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptType) {
            basicRequest = LibraryAPI_Util.getBasicLibrarianReq(acceptType, userType);

        this.acceptType = acceptType;
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        if (!endpoint.contains("/{")) {
            response = basicRequest
                    .when().get(endpoint);
        } else {
            response = pathParamRequest.when().get(endpoint);
        }

        this.endpoint = endpoint;
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        Integer actualStatusCode = response.statusCode();
        System.out.println("expectedStatusCode = " + expectedStatusCode);
        System.out.println("actualStatusCode = " + actualStatusCode);
        Assert.assertEquals(expectedStatusCode, actualStatusCode);

    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {
        String actualContentType = response.contentType();
        System.out.println("expectedContentType = " + expectedContentType);
        System.out.println("actualContentType = " + actualContentType);
        Assert.assertEquals(expectedContentType, actualContentType);
    }

    @Then("{string} field should not be null")
    public void field_should_not_be_null(String id) {

        basicRequest
                .when().get(endpoint)
                .then()
                .body(id, Matchers.everyItem(notNullValue()));
    }

    @And("Path param is {string}")
    public void pathParamIs(String pathParam) {
        pathParamRequest = RestAssured.given()
                .header("x-library-token", librarianToken)
                .pathParam("id", pathParam);

        this.pathParam = pathParam;
    }

    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String actualPathValue) {
        System.out.println("response.path(actualPathValue) = " + response.path(actualPathValue));
        System.out.println("expectedPathValue = " + pathParam);

        Assert.assertEquals(pathParam, response.path(actualPathValue));

    }

    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String> listOfResponseFields) {
        for (String eachResponseField : listOfResponseFields) {
            response.then()
                    .body(eachResponseField, notNullValue());
            System.out.println(eachResponseField + " field is not null");
            System.out.println("response.path(eachResponseField) = " + response.path(eachResponseField));
        }
    }
}