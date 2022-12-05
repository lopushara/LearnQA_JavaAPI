package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import lib.ApiCoreRequests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test trying to create user with invalid email")
    @DisplayName("Test negative creation. Invalid email")
    public void testCreateUserWithInvalidEmail() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", DataGenerator.getRandomInvalidEmail());
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Description("This test trying to create user without one of field")
    @DisplayName("Test negative creation. Without one of field")
    public void testCreateUserWithoutField(String keyField) {
        Map<String, String> userData = new HashMap<>();
        userData.put(keyField, null);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + keyField);
    }

    @Test
    @Description("This test trying to create user with short name")
    @DisplayName("Test negative creation. Short name")
    public void testCreateUserShortName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("username", "a");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("This test trying to create user with long name")
    @DisplayName("Test negative creation. Long name")
    public void testCreateUserLongName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("username", DataGenerator.getLongString(251));
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userdata = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userdata)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
}
