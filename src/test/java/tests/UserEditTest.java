package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    @Test
    @Description("This test created user and edit firstname field")
    @DisplayName("Positive create and edit user test")
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = ApiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = ApiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test trying to edit user without authorization")
    @DisplayName("Test negative edit. Edit user without authorization")
    public void testEditUserUnauthorized() {
        String newName = "Changed Name";
        Map<String, String> bodyData = new HashMap<>();
        bodyData.put("firstName", newName);

        Response responseEditUser = ApiCoreRequests.makePutRequestUnauthorized("https://playground.learnqa.ru/api/user/1", bodyData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @Description("This test authorize and trying to edit another user information")
    @DisplayName("Test negative edit. Edit another user")
    public void testEditAnotherUser() {
        //GENERATE USER_1
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", user1Data);
        String user1Id = responseCreateAuth.getString("id");

        //GENERATE USER_2
        Map<String, String> user2Data = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth2 = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", user2Data);
        String user2Id = responseCreateAuth2.getString("id");

        //LOGIN USER_1
        Map<String, String> authData = new HashMap<>();
        authData.put("email", user1Data.get("email"));
        authData.put("password", user1Data.get("password"));

        Response responseGetAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT USER_2 UNDER USER_1
        Map<String, String> editData = new HashMap<>();
        editData.put("username", "test");

        String tokenUser1 = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookieUser1 = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditUser = ApiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + user2Id,
                editData,
                tokenUser1,
                cookieUser1
        );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        Response responseUser2Data = ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + user2Id, tokenUser1, cookieUser1);
        Assertions.assertJsonByName(responseUser2Data, "username", "learnqa");


        //LOGIN UNDER USER_ADMIN
        Map<String, String> authDataAdmin = new HashMap<>();
        authDataAdmin.put("email", "vinkotov@example.com");
        authDataAdmin.put("password", "1234");

        Response responseGetAuthAdmin = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authDataAdmin);

        //EDIT USER_2 UNDER USER_ADMIN

        Map<String, String> editDataAdmin = new HashMap<>();
        editDataAdmin.put("username", "test");

        String tokenAdmin = this.getHeader(responseGetAuthAdmin, "x-csrf-token");
        String cookieAdmin = this.getCookie(responseGetAuthAdmin, "auth_sid");

        Response responseEditUserAdmin = ApiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + user2Id, editDataAdmin, tokenAdmin, cookieAdmin);

        Assertions.assertResponseCodeEquals(responseEditUserAdmin, 400);
        Assertions.assertResponseTextEquals(responseEditUserAdmin, "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
        ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + user2Id, tokenAdmin, cookieAdmin);
        Assertions.assertJsonByName(responseUser2Data, "username", "learnqa");

    }

    @Test
    @Description("This test trying to edit current user information to invalid email")
    @DisplayName("Negative test edition. Invalid email")
    public void testEditCurrentUserInvalidEmail() {
        //CREATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", userData);
        String userId = responseCreateAuth.getString("id");

        //LOGIN USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseGetUserBeforeEdit = ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, token, cookie);

        //EDIT EMAIL
        Map<String, String> editData = new HashMap<>();
        editData.put("email", DataGenerator.getRandomInvalidEmail());

        Response responseEditUser = ApiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, editData, token, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

        Response responseGetUserAfterEdit = ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, token, cookie);

        org.junit.jupiter.api.Assertions.assertEquals(responseGetUserBeforeEdit.asString(), responseGetUserAfterEdit.asString());
    }

    @Test
    @Description("This test trying to edit current user information to short firstName")
    @DisplayName("Negative test edition. Short firstName")
    public void testEditCurrentUserShort() {
        //CREATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", userData);
        String userId = responseCreateAuth.getString("id");

        //LOGIN USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseGetUserBeforeEdit = ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, token, cookie);

        //EDIT EMAIL
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "a");

        String expectedError = "{\"error\":\"Too short value for field firstName\"}";

        Response responseEditUser = ApiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, editData, token, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        org.junit.jupiter.api.Assertions.assertEquals(expectedError, responseEditUser.asString());

        Response responseGetUserAfterEdit = ApiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, token, cookie);

        org.junit.jupiter.api.Assertions.assertEquals(responseGetUserBeforeEdit.asString(), responseGetUserAfterEdit.asString());
    }
}
