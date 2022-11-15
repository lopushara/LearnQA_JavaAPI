import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;
import java.time.*;


public class HelloWorldTest {
    static String uri = "https://playground.learnqa.ru/ajax/api/longtime_job";
    @Test
    public void testRestAssured() throws InterruptedException {
        Map<String, String> headers = new HashMap<>();

        JsonPath response = RestAssured
                .given()
                .get(uri)
                .jsonPath();
        String token = response.get("token");
        int delay = response.get("seconds");
        System.out.println("Task will be finished in "+delay+" seconds");
        headers.put("token", token);

        JsonPath statusCheck = RestAssured
                .given()
                .queryParams(headers)
                .headers(headers)
                .get(uri)
                .jsonPath();
        String status = statusCheck.get("status");
        System.out.println("Check status Job = "+status);
        Thread.sleep(delay*1000);

        JsonPath getResult = RestAssured
                .given()
                .queryParams(headers)
                .get(uri)
                .jsonPath();
        status = getResult.get("status");
        String resultJson = getResult.get("result");
        System.out.println("Check status job after delay: "+status);
        System.out.println("Result = "+resultJson);

    }
}
