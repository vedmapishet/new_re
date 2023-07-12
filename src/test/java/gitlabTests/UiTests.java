package gitlabTests;


import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import io.github.bonigarcia.wdm.WebDriverManager;



import static com.codeborne.selenide.Selenide.$x;

@Tag("UI")
public class UiTests {

    @BeforeAll
    public static void setUp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");  
        options.addArguments("--disable-dev-shm-usage"); 
        options.addArguments("--start-maximized"); 
        Configuration.browserCapabilities = options;
        Configuration.headless = true;
    }

    @BeforeEach
    public void openPage(){
        Selenide.open("https://www.google.com/");
    }

    private void assertAnswer(String value){
        $x("//input[@name='q']").sendKeys(value + "=" + Keys.ENTER);
        String answer = $x("//span[@id='cwos']").getText();
        Assertions.assertEquals("4", answer);
    }

    @Test
    public void calcPlusTest() {
        assertAnswer("2+2");
    }

    @Test
    public void calcPlusTest2() {
        assertAnswer("1+3");
    }


    @Test
    public void calcMinusTest() {
        assertAnswer("6-2");
    }

    @Test
    public void calcMultipyTest() {
       assertAnswer("2*2");
    }

    @Test
    public void calcDevideTest() {
        assertAnswer("8/2");
    }
}
