import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex12 {
    String url = "https://playground.learnqa.ru/api/homework_header";
    @Test
    public void testRestAssuredEx11() {

        Response response = RestAssured
                .get(url)
                .andReturn();

        Headers getheaders = response.getHeaders();

        String headers = "Content-Type=application/json\n" +
        "Content-Length=15\n" +
        "Connection=keep-alive\n" +
        "Keep-Alive=timeout=10\n" +
        "Server=Apache\n" +
        "x-secret-homework-header=Some secret value\n" +
        "Cache-Control=max-age=0\n";

        assertTrue(getheaders.toString().contains(headers), "Unexpected response");
    }
}
