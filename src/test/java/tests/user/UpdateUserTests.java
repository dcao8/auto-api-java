package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.user.dao.CustomerAddressDao;
import model.user.dao.CustomerDao;
import model.user.dto.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import tests.TestMaster;
import utils.DbUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.ConstantUtils.*;
import static utils.DateTimeUtils.verifyDateTime;
import static utils.DateTimeUtils.verifyDateTimeDb;

public class UpdateUserTests extends TestMaster {

    @Test
    void verifyUpdateUserSuccessful() {
        //Prepare data
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format(EMAIL_TEMPLATE, randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        LocalDateTime timeBeforeCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        LocalDateTime timeBeforeCreateUserForDb = LocalDateTime.now();
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        assertThat(response.statusCode()).isEqualTo(200);
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        createdCustomerIds.add(createUserResponse.getId());
        //Perform updating
        UserRequest updateUserRequest = UserRequest.getUpdateUserInfo();
        updateUserRequest.setEmail(randomEmail);
        Response updateResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(updateUserRequest)
                .put(String.format(UPDATE_USER_API, createUserResponse.getId()));
        softAssertions = new SoftAssertions();
        //1. Verify Status code
        softAssertions.assertThat(updateResponse.statusCode()).isEqualTo(200);
        //2. Verify Header if needs
        softAssertions.assertThat(updateResponse.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(updateResponse.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify Body
        UpdateUserResponse updateUserResponse = updateResponse.as(UpdateUserResponse.class);
        softAssertions.assertThat(updateUserResponse.getId()).isEqualTo(createUserResponse.getId());
        softAssertions.assertThat(updateUserResponse.getMessage()).isEqualTo("Customer updated");
        //4. Double check that user has been stored in system
        Response getResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .get(String.format(GET_USER_API, createUserResponse.getId()));
        softAssertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        softAssertions.assertAll();
        GetUserResponse getUserResponse = getResponse.as(GetUserResponse.class);
        assertThatJson(getUserResponse).whenIgnoringPaths("$..id", "$..createdAt", "$..updatedAt", "$..customerId")
                .isEqualTo(updateUserRequest);
        softAssertions = new SoftAssertions();
        softAssertions.assertThat(getUserResponse.getId()).isEqualTo(createUserResponse.getId());
        LocalDateTime timeAfterCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        LocalDateTime timeAfterCreateUserForDb = LocalDateTime.now();
        for (GetUserAddressResponse getUserAddressResponse : getUserResponse.getAddresses()) {
            softAssertions.assertThat(getUserAddressResponse.getCustomerId()).isEqualTo(createUserResponse.getId());
            verifyDateTime(softAssertions, getUserAddressResponse.getCreatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
            verifyDateTime(softAssertions, getUserAddressResponse.getUpdatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        }
        verifyDateTime(softAssertions, getUserResponse.getCreatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        verifyDateTime(softAssertions, getUserResponse.getUpdatedAt(), timeBeforeCreateUser, timeAfterCreateUser);
        //5. Verify by access to DB
        CustomerDao customerDao = DbUtils.getCustomerFormDb(createUserResponse.getId());
        assertThatJson(customerDao).whenIgnoringPaths("$..id", "$..createdAt", "$..updatedAt", "$..customerId")
                .isEqualTo(updateUserRequest);
        softAssertions.assertThat(UUID.fromString(getUserResponse.getId())).isEqualTo(customerDao.getId());
        for (CustomerAddressDao addressDao : customerDao.getAddresses()) {
            softAssertions.assertThat(addressDao.getCustomerId()).isEqualTo(UUID.fromString(createUserResponse.getId()));
            verifyDateTimeDb(softAssertions, addressDao.getCreatedAt(), timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
            verifyDateTimeDb(softAssertions, addressDao.getUpdatedAt(), timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        }
        verifyDateTimeDb(softAssertions, customerDao.getCreatedAt(), timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        verifyDateTimeDb(softAssertions, customerDao.getUpdatedAt(), timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        softAssertions.assertAll();
    }
}
