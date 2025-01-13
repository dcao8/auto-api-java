package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.login.LoginRequest;
import model.login.LoginResponse;
import model.user.CreateUserResponse;
import model.user.UserRequest;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.RestAssuredUtils;

public class CreateUserTests {
    @BeforeAll
    static void setUp() {
        RestAssuredUtils.setUp();
    }

    @Test
    void verifyCreateUserSuccessful() {
        LoginRequest loginRequest = LoginRequest.getDefault();
        LoginResponse loginResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/login")
                .as(LoginResponse.class);
        String token = String.format("Bearer %s", loginResponse.getToken());
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format("api_%s@api.com", randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(userRequest)
                .post("/api/user");
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify Body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        softAssertions.assertAll();
    }
}
