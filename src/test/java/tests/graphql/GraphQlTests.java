package tests.graphql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static data.graphql.GraphQlData.EXPECTED_COUNTRY_QUERY_DATA;
import static data.graphql.GraphQlData.GET_COUNTRIES_QUERY;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class GraphQlTests {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://countries.trevorblades.com/";
    }

    @Test
    void verifyQueryCountriesSuccessful() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", GET_COUNTRIES_QUERY);
        Map<String, String> variables = new HashMap<>();
        variables.put("vncode", "VN");
        variables.put("brcode", "BR");
        requestBody.put("variables", variables);
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        System.out.println(response.asString());
        //2. Verify response header
        softAssertions.assertThat(response.header("stellate-rate-limit-decision")).isEqualTo("pass");
        softAssertions.assertAll();
        //3. Verify body schema
        //4. Verify body value
        assertThatJson(response.asString().equals(EXPECTED_COUNTRY_QUERY_DATA));
    }
}