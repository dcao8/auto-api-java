package utils;

import io.restassured.RestAssured;

import static utils.ConfigUtils.getDotenv;

public class RestAssuredUtils {
    public static void setUp() {
        RestAssured.baseURI = getDotenv().get("API_HOST");
        RestAssured.port = 3000;
    }
}