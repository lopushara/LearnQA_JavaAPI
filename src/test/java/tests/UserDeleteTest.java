package tests;

import io.qameta.allure.Description;
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

public class UserDeleteTest extends BaseTestCase {

    @Test
    @Description("This test trying to delete admin user")
    @DisplayName("Test negative delete. Delete admin user")
    public void testDeleteUserAdmin() {

        //LOGIN
        Map<String, String> authDataAdmin = new HashMap<>();
        authDataAdmin.put("email", "vinkotov@example.com");
        authDataAdmin.put("password", "1234");

        Response responseGetAuthAdmin = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authDataAdmin);

        String tokenAdmin = this.getHeader(responseGetAuthAdmin, "x-csrf-token");
        String cookieAdmin = this.getCookie(responseGetAuthAdmin, "auth_sid");

        //DELETE
        Response responseDeleteAdmin = ApiCoreRequests.makeDeleteRequest(" https://playground.learnqa.ru/api/user/2", tokenAdmin, cookieAdmin);

        Assertions.assertResponseCodeEquals(responseDeleteAdmin, 400);
        Assertions.assertResponseTextEquals(responseDeleteAdmin, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("This test create user and delete him")
    @DisplayName("Positive delete test. Create and delete user")
    public void testDeleteUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = ApiCoreRequests.makePostRequestJSON("https://playground.learnqa.ru/api/user", userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = ApiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //CHECK EXISTING USER

        Response responseGetUserBefore = ApiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertResponseCodeEquals(responseGetUserBefore, 200);

        //DELETE

        Response responseEditUser = ApiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseEditUser, 200);

        //CHECK EXISTING USER AFTER DELETING

        Response responseGetUserAfter = ApiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertResponseCodeEquals(responseGetUserAfter, 404);

    }

    @Test
    @Description("This test trying to delete user2 by user1")
    @DisplayName("Negative test delete. Delete another user")
    public void testDeleteAnotherUser() {
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

        //CHECK EXISTING USER

        Response responseGetUserBefore = ApiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + user2Id,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertResponseCodeEquals(responseGetUserBefore, 200);

        //DELETE USER_2 BY USER_1

        String tokenUser1 = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookieUser1 = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = ApiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + user2Id,
                tokenUser1,
                cookieUser1
        );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Some text error that should be here");

        Response responseGetUserAfter = ApiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + user2Id,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertResponseCodeEquals(responseGetUserAfter, 200);

    }

}
