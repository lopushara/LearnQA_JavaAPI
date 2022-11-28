import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

public class Ex11 {
    String url = "https://playground.learnqa.ru/api/homework_cookie";
    @Test
    public void testRestAssuredEx11() {
        Map<String, String> responsecookies = new HashMap<>();
        responsecookies.put("HomeWork","hw_value");
        Response response = RestAssured
                .get(url)
                .andReturn();
        assertEquals(responsecookies, response.getCookies(), "Unexpected cookies");
    }
}
