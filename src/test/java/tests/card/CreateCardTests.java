package tests.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.card.CreateCardRequest;
import model.card.CreateCardResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import tests.TestMaster;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static utils.ConstantUtils.*;

public class CreateCardTests extends TestMaster {
    @Test
    void verifyCreateCardSuccessful() throws JsonProcessingException {
        WireMockServer refDataServer = new WireMockServer(options().port(7777).notifier(new ConsoleNotifier(true)));
        refDataServer.start();
        WireMockServer cardServer = new WireMockServer(options().port(7778).notifier(new ConsoleNotifier(true)));
        cardServer.start();
        CreateCardRequest createCardRequest = new CreateCardRequest("fd94a420-445f-4ea0-8bfa-2bf4eec8c982", "SILVER");
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
        CreateCardResponse expectedCreateCardResponse = new CreateCardResponse("1111 2222 3333 4444", "John Cena", "01-02-2025");
        softAssertions.assertThat(createCardResponse).isEqualTo(expectedCreateCardResponse);
        softAssertions.assertAll();
    }
}
