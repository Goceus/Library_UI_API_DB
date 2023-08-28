package com.library.utility;

import com.github.javafaker.Faker;
import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LibraryAPI_Util {

    /**
     * Method returns RequestSpecification as application/json
     *
     * @return
     */
    public static Response getAllLibrariansResponse(String endpoint) {
        return given()
                .header(ConfigurationReader.getProperty("apiKeyName"), getToken("librarian"))
                .accept("application/json")
                .when().get(endpoint);
    }

    /**
     * Method returns a librarian as a Response by specifying librarian ID.
     *
     * @param pathParam
     * @return
     */
    public static Response getLibrarianByIDResponse(String pathParam, String pathValue, String endpoint) {
        return given()
                .header(ConfigurationReader.getProperty("apiKeyName"), getToken("librarian"))
                .accept("application/json")
                .pathParam(pathParam, pathValue)
                .when().get(endpoint);
    }

    /**
     * Method returns basic RequestSpecification and accepts application/json format.
     * @return
     */
    public static RequestSpecification getBasicLibrarianReq(String userType, String acceptType) {
        return given()
                .header(ConfigurationReader.getProperty("apiKeyName"), getToken("librarian"))
                .accept(acceptType);
    }


    /**
     * Return TOKEN as String by using provided username from /token endpoint
     *
     * @param userType
     * @return
     */
    public static String getToken(String userType) {


        String email = ConfigurationReader.getProperty(userType + "_username");
        String password = ConfigurationReader.getProperty(userType + "_password");


        return getToken(email, password);


    }

    public static String getToken(String email, String password) {


        return given()
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password).
                when()
                .post(ConfigurationReader.getProperty("library.baseUri") + "/login")
                .prettyPeek()
                .path("token");


    }

    public static Map<String, Object> getRandomBookMap() {

        Faker faker = new Faker();
        Map<String, Object> bookMap = new LinkedHashMap<>();
        String randomBookName = faker.book().title() + faker.number().numberBetween(0, 10);
        bookMap.put("name", randomBookName);
        bookMap.put("isbn", faker.code().isbn10());
        bookMap.put("year", faker.number().numberBetween(1000, 2021));
        bookMap.put("author", faker.book().author());
        bookMap.put("book_category_id", faker.number().numberBetween(1, 20));  // in library app valid category_id is 1-20
        bookMap.put("description", faker.chuckNorris().fact());

        return bookMap;
    }

    public static Map<String, Object> getRandomUserMap() {

        Faker faker = new Faker();
        Map<String, Object> bookMap = new LinkedHashMap<>();
        String fullName = faker.name().fullName();
        String email = fullName.substring(0, fullName.indexOf(" ")) + "@library";
        System.out.println(email);
        bookMap.put("full_name", fullName);
        bookMap.put("email", email);
        bookMap.put("password", "libraryUser");
        // 2 is librarian as role
        bookMap.put("user_group_id", 2);
        bookMap.put("status", "ACTIVE");
        bookMap.put("start_date", "2023-03-11");
        bookMap.put("end_date", "2024-03-11");
        bookMap.put("address", faker.address().cityName());

        return bookMap;
    }


}
