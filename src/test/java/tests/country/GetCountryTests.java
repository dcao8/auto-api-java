package tests.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import model.country.CountriesPagination;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tests.TestMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static data.country.GetCountryData.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class GetCountryTests extends TestMaster {

    @Test
    void verifyGetCountryApiSchema() {
        RestAssured.given().log().all()
                .get("/api/v1/countries")
                .then().log().all()
                .statusCode(200)
                .assertThat().body(matchesJsonSchemaInClasspath("data/get-country/get-country-json-schema.json"));
    }

    @Test
    void verifyGetCountryApiResponseCorrectData() throws JsonProcessingException {
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> expectedData = mapper.readValue(GET_ALL_COUNTRIES, new TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> responseData = mapper.readValue(response.asString(), new TypeReference<List<Map<String, Object>>>() {
        });
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        softAssertions.assertThat(responseData).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedData);
        softAssertions.assertAll();
    }

    @Test
    void verifyGetCountryWithGdpApiSchema() {
        RestAssured.given().log().all()
                .get("/api/v2/countries")
                .then().log().all()
                .statusCode(200)
                .assertThat().body(matchesJsonSchemaInClasspath("data/get-country/get-country-with-gdp-json-schema.json"));
    }

    @Test
    void verifyGetCountryWithGdpApiResponseCorrectData() throws JsonProcessingException {
        Response response = RestAssured.given().log().all()
                .get("/api/v2/countries");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> expectedData = mapper.readValue(GET_ALL_COUNTRIES_WITH_GDP, new TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> responseData = mapper.readValue(response.asString(), new TypeReference<List<Map<String, Object>>>() {
        });
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        //softAssertions.assertThat(response.asString()).isEqualTo(jsonEquals(GET_ALL_COUNTRIES_WITH_GDP).when(Option.IGNORING_ARRAY_ORDER));
        softAssertions.assertThat(responseData).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedData);
        softAssertions.assertAll();
    }

    static Stream<Map<String, String>> countryProvider() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> data = mapper.readValue(GET_ALL_COUNTRIES, new TypeReference<List<Map<String, String>>>() {
        });
        return data.stream();
    }

    @ParameterizedTest
    @MethodSource("countryProvider")
    void verifyGetCountryByCodeApiResponseCorrectData(Map<String, String> country) throws JsonProcessingException {
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries/{code}", country.get("code"));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responseBody = mapper.readValue(response.asString(), new TypeReference<Map<String, String>>() {
        });
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        //softAssertions.assertThat(response.asString()).isEqualTo(jsonEquals(country));
        softAssertions.assertThat(responseBody).isEqualTo(country);
        softAssertions.assertAll();
    }

    static Stream<?> verifyGetCountriesWithFilter() {
        List<Map<String, String>> data = new ArrayList<>();
        data.add(Map.of("gdp", "1868", "operator", ">"));
        data.add(Map.of("gdp", "1868", "operator", "<"));
        data.add(Map.of("gdp", "1868", "operator", ">="));
        data.add(Map.of("gdp", "1868", "operator", "<="));
        data.add(Map.of("gdp", "1868", "operator", "=="));
        data.add(Map.of("gdp", "1868", "operator", "!="));
        return data.stream();
    }

    @ParameterizedTest
    @MethodSource("verifyGetCountriesWithFilter")
    void verifyGetCountriesWithFilter(Map<String, String> queryParams) {
        Response response = RestAssured.given().log().all()
                .queryParams(queryParams)
                .get("/api/v3/countries");
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        List<Map<String, String>> countries = response.as(new TypeRef<List<Map<String, String>>>() {
        });
        for (Map<String, String> country : countries) {
            float actualGdp = Float.parseFloat(queryParams.get("gdp"));
            float expectedGdp = Float.parseFloat(country.get("gdp"));
            switch (queryParams.get("operator")) {
                case ">" -> softAssertions.assertThat(expectedGdp).isGreaterThan(actualGdp);
                case "<" -> softAssertions.assertThat(expectedGdp).isLessThan(actualGdp);
                case ">=" -> softAssertions.assertThat(expectedGdp).isGreaterThanOrEqualTo(actualGdp);
                case "<=" -> softAssertions.assertThat(expectedGdp).isLessThanOrEqualTo(actualGdp);
                case "==" -> softAssertions.assertThat(expectedGdp).isEqualTo(actualGdp);
                default -> softAssertions.assertThat(expectedGdp).isNotEqualTo(actualGdp);
            }
        }
        softAssertions.assertAll();
    }

    @Test
    void verifyGetCountriesWithPagination() {
        int page = 1;
        int size = 4;
        //Verify the first page
        Response response = getCountries(page, size);
        CountriesPagination countriesFirstPage = response.as(CountriesPagination.class);
        verifyCountriesResponse(response, countriesFirstPage, size);

        //Verify the second page
        response = getCountries(page + 1, size);
        CountriesPagination countriesSecondPage = response.as(CountriesPagination.class);
        verifyCountriesResponse(response, countriesSecondPage, size);

        //Verify data of both pages are not the same
        softAssertions = new SoftAssertions();
        softAssertions.assertThat(countriesSecondPage.getData().containsAll(countriesFirstPage.getData())).isFalse();

        //Verify the last page
        int total = countriesFirstPage.getTotal();
        int lastPage = total / size;
        if (total % size != 0) {
            lastPage++;
        }
        int sizeOfLastPage = total % size;
        if (sizeOfLastPage == 0) {
            sizeOfLastPage = size;
        }
        response = getCountries(lastPage, sizeOfLastPage);
        CountriesPagination countriesLastPage = response.as(CountriesPagination.class);
        verifyCountriesResponse(response, countriesLastPage, sizeOfLastPage);
        softAssertions.assertAll();
    }

    private static void verifyCountriesResponse(Response response, CountriesPagination countriesPage, int size) {
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        softAssertions.assertThat(countriesPage.getData().size()).isEqualTo(size);
        softAssertions.assertAll();
    }

    private static Response getCountries(int page, int size) {
        return RestAssured.given().log().all()
                .queryParam("page", page)
                .queryParam("size", size)
                .get("/api/v4/countries");
    }

    @Test
    void verifyGetCountriesWithPrivateKey() throws JsonProcessingException {
        Response response = RestAssured.given().log().all()
                .header("api-key", "private")
                .get("/api/v5/countries");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> expectedData = mapper.readValue(GET_ALL_COUNTRIES_WITH_PRIVATE, new TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> responseData = mapper.readValue(response.asString(), new TypeReference<List<Map<String, Object>>>() {
        });
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        softAssertions.assertThat(responseData).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedData);
        softAssertions.assertAll();
    }
}