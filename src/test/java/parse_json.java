import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;


public class parse_json {

    @Test
    public void testRestAssured(){
        JsonPath response = RestAssured
                .given()
                .queryParam("messages")
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String message = response.get("messages");
        System.out.println(message);
    }
}
