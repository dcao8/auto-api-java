package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.login.LoginFailResponse;
import model.login.LoginRequest;
import model.login.LoginResponse;
import model.user.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import utils.LoginUtils;
import utils.RestAssuredUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class CreateUserTests {
    SoftAssertions softAssertions;
    static String token;
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String HEADER_POWER_BY = "X-Powered-By";
    static final String CONTENT_TYPE = "application/json; charset=utf-8";
    static final String POWER_BY = "Express";
    static final String EMAIL_TEMPLATE = "api_%s@api.com";
    static final String CREATE_USER_API = "/api/user";
    static final String GET_USER_API = "/api/user/%s";

    @BeforeAll
    static void setUp() {
        RestAssuredUtils.setUp();
    }

    @BeforeEach
    void beforeEach() {
        token = LoginUtils.getToken();
    }

    @Test
    void verifyCreateUserSuccessful() {
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format(EMAIL_TEMPLATE, randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        LocalDateTime timeBeforeCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify Body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        //4. Double check that user has been stored in system
        Response getResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .get(String.format(GET_USER_API, createUserResponse.getId()));
        softAssertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        softAssertions.assertAll();
        GetUserResponse getUserResponse = getResponse.as(GetUserResponse.class);
        assertThatJson(getUserResponse).whenIgnoringPaths("$..id", "$..createdAt", "$..updatedAt", "$..customerId")
                .isEqualTo(userRequest);
        softAssertions = new SoftAssertions();
        softAssertions.assertThat(getUserResponse.getId()).isEqualTo(createUserResponse.getId());
        LocalDateTime timeAfterCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        for (GetUserAddressResponse getUserAddressResponse : getUserResponse.getAddresses()) {
            softAssertions.assertThat(getUserAddressResponse.getCustomerId()).isEqualTo(createUserResponse.getId());
            verifyDateTime(softAssertions, getUserAddressResponse.getCreatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
            verifyDateTime(softAssertions, getUserAddressResponse.getUpdatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        }
        verifyDateTime(softAssertions, getUserResponse.getCreatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        verifyDateTime(softAssertions, getUserResponse.getUpdatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        softAssertions.assertAll();
    }

    void verifyDateTime(SoftAssertions softAssertions, String targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter) {
        LocalDateTime localDateTime = LocalDateTime.parse(targetDateTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        softAssertions.assertThat(localDateTime.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(localDateTime.isBefore(timeAfter)).isTrue();
    }

    @Test
    void verifyCreateUserSuccessfulWithTwoAddress() {
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format(EMAIL_TEMPLATE, randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        UserAddressRequest userAddressRequest1 = UserAddressRequest.getDefault();
        UserAddressRequest userAddressRequest2 = UserAddressRequest.getDefault();
        userAddressRequest2.setStreetNumber("456");
        userRequest.setAddresses(List.of(userAddressRequest1, userAddressRequest2));
        userRequest.setEmail(randomEmail);
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify Body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        softAssertions.assertAll();
    }

    @ParameterizedTest
    @CsvSource({
            "/firstName, '',must NOT have fewer than 1 characters",
            "/lastName, '',must NOT have fewer than 1 characters",
            "/middleName, '',must NOT have fewer than 1 characters",
            "/birthday, '',must match pattern \"^\\d{2}-\\d{2}-\\d{4}$\"",
            "/email, '',must match format \"email\"",
            "/phone, '',must match pattern \"^\\d{10,11}$\"",
            "/addresses/0/streetNumber, '',must NOT have fewer than 1 characters",
            "/addresses/0/street, '',must NOT have fewer than 1 characters",
            "/addresses/0/ward, '',must NOT have fewer than 1 characters",
            "/addresses/0/district, '',must NOT have fewer than 1 characters",
            "/addresses/0/city, '',must NOT have fewer than 1 characters",
            "/addresses/0/state, '',must NOT have fewer than 1 characters",
            "/addresses/0/zip, '',must match pattern \"^\\d{5}(?:-\\d{4})?$\"",
            "/addresses/0/country, '',must NOT have fewer than 2 characters",
    })
    void checkValidateMessage(String field, String valueInput, String message) {
        UserRequest userRequest = UserRequest.getDefault();
        UserAddressRequest userAddressRequest = UserAddressRequest.getDefault();
        switch (field) {
            case "/firstName" -> userRequest.setFirstName(valueInput);
            case "/lastName" -> userRequest.setLastName(valueInput);
            case "/middleName" -> userRequest.setMiddleName(valueInput);
            case "/birthday" -> userRequest.setBirthday(valueInput);
            case "/email" -> userRequest.setEmail(valueInput);
            case "/phone" -> userRequest.setPhone(valueInput);
            case "/addresses/0/streetNumber" -> userAddressRequest.setStreetNumber(valueInput);
            case "/addresses/0/street" -> userAddressRequest.setStreet(valueInput);
            case "/addresses/0/ward" -> userAddressRequest.setWard(valueInput);
            case "/addresses/0/district" -> userAddressRequest.setDistrict(valueInput);
            case "/addresses/0/city" -> userAddressRequest.setCity(valueInput);
            case "/addresses/0/state" -> userAddressRequest.setState(valueInput);
            case "/addresses/0/zip" -> userAddressRequest.setZip(valueInput);
            case "/addresses/0/country" -> userAddressRequest.setCountry(valueInput);
        }
        userRequest.setAddresses(List.of(userAddressRequest));
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(400);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify Body
        CreateUserFailResponse createUserFailResponse = response.as(CreateUserFailResponse.class);
        softAssertions.assertThat(createUserFailResponse.getField()).isEqualTo(field);
        softAssertions.assertThat(createUserFailResponse.getMessage()).isEqualTo(message);
        softAssertions.assertAll();
    }
}