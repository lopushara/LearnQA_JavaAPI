import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class HelloWorldTest {
    static int statusCode;
    static String uri = "https://playground.learnqa.ru/api/long_redirect";
    @Test
    public void testRestAssured(){
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

         do {

            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .headers(headers)
                    .when()
                    .get(uri)
                    .andReturn();
            statusCode = response.getStatusCode();
            System.out.println(uri);
            System.out.println(statusCode);
            if (response.getHeader("Location") != null) {
                uri = response.getHeader("Location");
            }
            else {
                return;
            }
        }
         while (statusCode != 200);

    }
}
