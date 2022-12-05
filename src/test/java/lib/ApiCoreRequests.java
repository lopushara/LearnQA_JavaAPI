package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-request with token and auth cookie")
    public static Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public static Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth token only")
    public static Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", token)
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public static Response makePostRequest(String url, Map<String, String> bodyData) {
        return given()
                .filter(new AllureRestAssured())
                .body(bodyData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with JSON")
    public static JsonPath makePostRequestJSON(String url, Map<String, String> bodyData) {
        return given()
                .filter(new AllureRestAssured())
                .body(bodyData)
                .post(url)
                .jsonPath();
    }

    @Step("Make a PUT-request")
    public static Response makePutRequestUnauthorized(String url, Map<String, String> bodyData) {
        return given()
                .filter(new AllureRestAssured())
                .body(bodyData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request with authorization")
    public static Response makePutRequest(String url, Map<String, String> bodyData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(bodyData)
                .put(url)
                .andReturn();
    }
}
