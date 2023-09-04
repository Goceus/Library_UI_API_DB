package com.library.steps;


import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.DB_Util;
import com.library.utility.Driver;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.Map;
import static org.hamcrest.Matchers.notNullValue;

public class API_POST_StepDeff {

    RequestSpecification librarianBasicCTRequest;
    RequestSpecification creationRequestWithBody;
    LoginPage loginPage = new LoginPage();
    BookPage bookPage = new BookPage();
    Map<String, Object> createdMap;
    Response createdBookFromAPI;
    Response response;
    String bookID;

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentType) {
        librarianBasicCTRequest = LibraryAPI_Util.getBasicUserReq("librarian", ContentType.JSON.toString()).contentType(contentType);


    }

    @Given("I create a random book as request body")
    public void i_create_a_random_as_request_body() {
        createdMap = LibraryAPI_Util.getRandomBookMap();
        creationRequestWithBody = librarianBasicCTRequest
                .formParams(createdMap);
        System.out.println("createdMap = " + createdMap);
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
        bookID = response.path("book_id");
        System.out.println("bookID = " + bookID);
    }

    @Then("status code should be {string}")
    public void statusCodeShouldBe(String expectedStatusCode) {
        int actualStatusCode = response.statusCode();
        String actStatusCode = "" + actualStatusCode;
        System.out.println("actStatusCode = " + actStatusCode);
        System.out.println("expectedStatusCode = " + expectedStatusCode);
        Assert.assertEquals(expectedStatusCode, actStatusCode);
    }

    @And("Created book Response Content type is {string}")
    public void createdBookResponseContentTypeIs(String expectedContentType) {
        String actualContentType = response.contentType();
        System.out.println("expectedContentType = " + expectedContentType);
        System.out.println("actualContentType = " + actualContentType);
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

    @Given("I logged in Library UI as {string}")
    public void
    i_logged_in_library_ui_as(String userType) {
        loginPage.login(userType);
    }

    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String navModule) {
        loginPage.navigateModule(navModule);
    }

    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {
        bookPage.search.sendKeys((String) createdMap.get("name") + Keys.ENTER);
        //bookPage.editBook((String) createdMap.get("name")).click();
        //BrowserUtil.waitFor(4);
        BrowserUtil.waitForVisibility(bookPage.editBook(""+createdMap.get("isbn")), 10);

        // UI expected data:
        String uiBookName = bookPage.getCellText((String) createdMap.get("name"));
        System.out.println("expectedBookName = " + uiBookName);
        String uiISBN = bookPage.getCellText((String) createdMap.get("isbn"));
        System.out.println("expectedISBN = " + uiISBN);
        String uiYear = bookPage.getCellText(""+createdMap.get("year"));
        System.out.println("expectedYear = " + uiYear);
        String uiAuthor = bookPage.getCellText(""+createdMap.get("author"));
        System.out.println("expectedAuthor = " + uiAuthor);
        String uiCatName = bookPage.getCategoryName((int)createdMap.get("book_category_id")).getText();
        System.out.println("expectedCatName = " + uiCatName);
        //Getting expected book description:
        bookPage.search.sendKeys((String)createdMap.get("name") + Keys.ENTER);
        //BrowserUtil.waitFor(3);
        BrowserUtil.waitForVisibility(bookPage.editBook(""+createdMap.get("isbn")), 10);
        bookPage.editBook((String)createdMap.get("isbn")).click();
        //BrowserUtil.waitFor(3);
        BrowserUtil.waitForVisibility(Driver.getDriver().findElement(By.xpath("//textarea[@id='description']")), 10);
        String uiBookDescription = bookPage.getBookDescription((String)createdMap.get("name"));
        System.out.println("expectedBookDescription = " + uiBookDescription);
        System.out.println("==========================================================================================");

        //API Actual data
        createdBookFromAPI = LibraryAPI_Util.getBasicUserReq("librarian", ContentType.JSON.toString())
                .pathParam("id", bookID)
                .when().get("/get_book_by_id/{id}").prettyPeek();

        String actualAPIBookName = createdBookFromAPI.path("name");
        String actualAPIISBN = createdBookFromAPI.path("isbn");
        String actualAPIBookYear = createdBookFromAPI.path("year");
        String actualAPIAuthor = createdBookFromAPI.path("author");
        String actualAPIBookCategoryNum = createdBookFromAPI.path("book_category_id");
        String actualAPIBookCatName = bookPage.getCategoryName(Integer.parseInt(actualAPIBookCategoryNum)).getText();
        String actualBookDescription = createdBookFromAPI.path("description");

        //Assertions:
        Assert.assertEquals(uiBookName, actualAPIBookName);
        Assert.assertEquals(uiISBN, actualAPIISBN);
        Assert.assertEquals(uiYear, actualAPIBookYear);
        Assert.assertEquals(uiAuthor, actualAPIAuthor);
        Assert.assertEquals(uiBookDescription, actualBookDescription);
        Assert.assertEquals(uiCatName, actualAPIBookCatName);

        //DB Testing
        DB_Util.createConnection();
        //run query
        DB_Util.runQuery("SELECT b.id, b.name, b.isbn, b.year, b.author, b.book_category_id, b.description\n" +
                "FROM book_borrow bb right outer join books b on bb.id=b.id\n" +
                "WHERE b.id="+bookID);
        Map<String, Object> expectedDBBookMap = DB_Util.getRowMap(1);
        System.out.println("expectedDBBookMap = " + expectedDBBookMap);

        //Assertions UI against DB:
        String expectedDBBookName = (String)expectedDBBookMap.get("name");
        String expectedDBISBN = (String)expectedDBBookMap.get("isbn");
        String expectedDBYear = (String)expectedDBBookMap.get("year");
        String expectedDBAuthor = (String)expectedDBBookMap.get("author");
        String expectedDBDescription = (String)expectedDBBookMap.get("description");
        String expectedDBBookID = (String)expectedDBBookMap.get("book_category_id");
        System.out.println("expectedDBBookID = " + expectedDBBookID);
        String expectedDBCatName = bookPage.getCategoryName(Integer.parseInt(expectedDBBookID)).getText();
        System.out.println("expectedDBCatName = " + expectedDBCatName);
        System.out.println("uiCatName = " + uiCatName);

        //Assertions:
        Assert.assertEquals(expectedDBBookName, uiBookName);
        Assert.assertEquals(expectedDBISBN,uiISBN);
        Assert.assertEquals(expectedDBYear, uiYear);
        Assert.assertEquals(expectedDBAuthor, uiAuthor);
        Assert.assertEquals(expectedDBDescription, uiBookDescription);
        Assert.assertEquals(expectedDBCatName, uiCatName);
    }
}

