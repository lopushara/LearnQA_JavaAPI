import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.*;


public class HelloWorldTest {
    static String uri = "https://playground.learnqa.ru/api/get_json_homework";
    @Test
    public void testRestAssured() {
        ArrayList<LinkedHashMap<String , String>> messages;
        Map<String, String> secondMessage;
        JsonPath response = RestAssured
                .given()
                .get(uri)
                .jsonPath();
        messages=response.get("messages");
        secondMessage=messages.get(1);
        String finalMessage = secondMessage.get("message");
        System.out.println(finalMessage);

    }
}
