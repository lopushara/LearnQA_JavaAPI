import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10 {
    static String stroka = "more than 15 chars";

    @Test
    public void Ex10(){
        assertTrue(stroka.length()>15, "Less than 15");
    }

}
