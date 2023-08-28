package com.library.steps;

import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matchers;
import org.junit.Assert;

public class RetreiveAllUsers_API {

//    LibraryAPI_Util libraryAPIUtil = new LibraryAPI_Util();
    String librarianToken;
    Response response;
    String acceptType;
    String endpoint;


    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        librarianToken = LibraryAPI_Util.getToken(userType);
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptType) {
        RequestSpecification accept = RestAssured.given()
                .header("x-library-token", librarianToken)
                .accept(acceptType);
        this.acceptType = acceptType;
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = RestAssured.given()
                .header("x-library-token", librarianToken)
                .accept(acceptType)
                .when().get(endpoint);
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

        RestAssured.given()
                .header("x-library-token", librarianToken)
                .accept(acceptType)
                .when().get(endpoint)
                .then()
                .body(id, Matchers.everyItem(notNullValue()));
    }

}
