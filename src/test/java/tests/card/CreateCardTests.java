package tests.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.card.CreateCardRequest;
import model.card.CreateCardResponse;
import model.user.dto.CreateUserResponse;
import model.user.dto.UserRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.TestMaster;
import utils.StubUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.ConstantUtils.*;

public class CreateCardTests extends TestMaster {

    @BeforeAll
    static void setUpForCard() {
        StubUtils.startStubForCreateCard();
    }

    @Test
    void verifyCreateCardSuccessful() throws JsonProcessingException {
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format(EMAIL_TEMPLATE, randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        userRequest.setFirstName("Jos");
        userRequest.setLastName("Doe");
        Response createUserResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        assertThat(createUserResponse.statusCode()).isEqualTo(200);
        CreateUserResponse createUserResponseBody = createUserResponse.as(CreateUserResponse.class);
        createdCustomerIds.add(createUserResponseBody.getId());
        CreateCardRequest createCardRequest = new CreateCardRequest(createUserResponseBody.getId(), "SILVER");
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(createCardRequest)
                .post(CREATE_CARD_API);
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify Body
        CreateCardResponse createCardResponse = response.as(CreateCardResponse.class);
        CreateCardResponse expectedCreateCardResponse = new CreateCardResponse("1111 2222 3333 4444", String.format("%s %s", userRequest.getLastName(), userRequest.getFirstName()), "01-02-2025");
        softAssertions.assertThat(createCardResponse).isEqualTo(expectedCreateCardResponse);
        softAssertions.assertAll();
    }
}
