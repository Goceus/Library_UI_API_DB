package com.library.steps;

import com.github.javafaker.Faker;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;
import static org.hamcrest.Matchers.notNullValue;

public class API_POST_StepDeff {

    RequestSpecification librarianBasicCTRequest;
    RequestSpecification creationRequestWithBody;
    Response response;

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentType) {
        librarianBasicCTRequest = LibraryAPI_Util.getBasicUserReq("librarian", ContentType.JSON.toString()).contentType(contentType);


    }

    @Given("I create a random book as request body")
    public void i_create_a_random_as_request_body() {
        creationRequestWithBody = librarianBasicCTRequest
                .formParams(LibraryAPI_Util.getRandomBookMap());
//        Faker faker = new Faker();
//        String randomBookName = faker.book().title();
//        creationRequestWithBody = librarianBasicCTRequest
//                .formParam("name", randomBookName)
//                .formParam("isbn", faker.code().isbn10())
//                .formParam("year", faker.number().numberBetween(1000, 2021))
//                .formParam("author", faker.book().author())
//                .formParam("book_category_id", faker.number().numberBetween(1, 20))
//                .formParam("description", faker.chuckNorris().fact());

    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String endpoint) {
        response = creationRequestWithBody.when().post(endpoint);
    }

    @Then("status code should be {string}")
    public void statusCodeShouldBe(String expectedStatusCode) {
        int actualStatusCode = response.statusCode();
        String actStatusCode = "" + actualStatusCode;
        System.out.println("actStatusCode = " + actStatusCode);
        System.out.println("expectedStatusCode = " + expectedStatusCode);
        Assert.assertEquals(expectedStatusCode, actStatusCode);
    }

    @And("Response Content type is listed as {string}")
    public void responseContentTypeIsListedAs(String expectedContentType) {
        String actualContentType = response.contentType();
        System.out.println("actualContentType = " + actualContentType);
        System.out.println("expectedContentType = " + expectedContentType);

        Assert.assertEquals(expectedContentType, actualContentType);

    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String key, String expectedValue) {
        String actualPathValue = response.path(key);
        System.out.println("actualPathValue = " + actualPathValue);
        System.out.println("expectedJsonValue = " + expectedValue);

        Assert.assertEquals(expectedValue, actualPathValue);
    }


    @And("{string} field should not have value null")
    public void fieldShouldNotHaveValueNull(String responseKey) {
        response.then().body(responseKey, Matchers.is(notNullValue()));
        System.out.println(responseKey + " is not null");
    }

//
//    @And("I navigate to {string} page")
//    public void iNavigateToPage(String arg0) {
//
//    }
//
//    @And("I logged in Library UI as {string}")
//    public void iLoggedInLibraryUIAs(String arg0) {
//
//    }
//
//    @And("UI, Database and API created book information must match")
//    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {
//    }
}
