package country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import model.country.CountriesPagination;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static data.country.GetCountryData.GET_ALL_COUNTRIES;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetCountryTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void verifyGetCountryApiSchema() {
        RestAssured.given().log().all()
                .get("/api/v1/countries")
                .then().log().all()
                .statusCode(200)
                .assertThat().body(matchesJsonSchemaInClasspath("data/get-country/get-country-json-schema.json"));
    }

    @Test
    void verifyGetCountryApiResponseCorrectData() {
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries");
        //1. Verify Status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify Header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify Body
        assertThat(response.asString(), jsonEquals(GET_ALL_COUNTRIES).when(Option.IGNORING_ARRAY_ORDER));
    }

    static Stream<Map<String, String>> countryProvider() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> data = mapper.readValue(GET_ALL_COUNTRIES, new TypeReference<List<Map<String, String>>>() {
        });
        return data.stream();
    }

    @ParameterizedTest
    @MethodSource("countryProvider")
    void verifyGetCountriesApiResponseCorrectData(Map<String, String> country) {
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries/{code}", country.get("code"));
        //1. Verify Status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify Header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify Body
        assertThat(response.asString(), jsonEquals(country));
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
        //1. Verify Status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify Header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify Body
        List<Map<String, String>> countries = response.as(new TypeRef<List<Map<String, String>>>() {
        });
        for (Map<String, String> country : countries) {
            float actualGdp = Float.parseFloat(queryParams.get("gdp"));
            Matcher<Float> matcher = switch (queryParams.get("operator")) {
                case ">" -> greaterThan(actualGdp);
                case "<" -> lessThan(actualGdp);
                case ">=" -> greaterThanOrEqualTo(actualGdp);
                case "<=" -> lessThanOrEqualTo(actualGdp);
                case "==" -> equalTo(actualGdp);
                default -> not(equalTo(actualGdp));
            };
            assertThat(Float.parseFloat(country.get("gdp")), matcher);
        }
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
        assertThat(countriesSecondPage.getData().containsAll(countriesFirstPage.getData()), is(false));

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
    }

    private static void verifyCountriesResponse(Response response, CountriesPagination countriesPage, int size) {
        //1. Verify Status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify Header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify Body
        assertThat(countriesPage.getData().size(), equalTo(size));
    }

    private static Response getCountries(int page, int size) {
        return RestAssured.given().log().all()
                .queryParam("page", page)
                .queryParam("size", size)
                .get("/api/v4/countries");
    }
}