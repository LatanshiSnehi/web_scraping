package demo;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;
// import dev.failsafe.internal.util.Assert;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrappers ;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest(alwaysRun = true)
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @BeforeMethod(alwaysRun = true)
    public void naviagation() throws InterruptedException{
        wrappers = new Wrappers(driver);
        wrappers.navigateToUrl();
        Thread.sleep(2000);
    }

    @Test(description = "Testcase01" , enabled = true)
    public void testCase01() throws InterruptedException, StreamWriteException, DatabindException, IOException{
        wrappers.clickOnPageTitle("Hockey Teams: Forms, Searching and Pagination");
        Thread.sleep(2000);

        List<Map<String, Object>> teamdata = wrappers.getTableData();

        //converting data to json 
       ObjectMapper objectMapper = new ObjectMapper();
       objectMapper.writeValue(new File("Hockey_team.json"), teamdata);    
    }

    @Test(description = "Testcase02", enabled = true)
    public void testCase02() throws InterruptedException{
        wrappers.clickOnPageTitle("Oscar Winning Films: AJAX and Javascript");
        wrappers.scrapeMovieData();
        wrappers.writeDataToJson();

        Thread.sleep(3000);

        File jsonFile = new File("output/oscar_winning_movies.json");
        Assert.assertTrue(jsonFile.exists(), "Json file doesnt exist");
        Assert.assertTrue(jsonFile.length() > 0, "JSON file is empty.");
    }

    @AfterTest(alwaysRun = true)
    public void endTest()
    {
        //driver.close();
        driver.quit();

    }
}