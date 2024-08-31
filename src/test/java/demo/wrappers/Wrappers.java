package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.failsafe.internal.util.Durations;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class Wrappers {
    WebDriver driver;
    WebDriverWait wait;

    @FindBy(className = "page-title")
    private List<WebElement> pageTitles;

    @FindBy(xpath = "//table//tbody//tr//td[1]")
    private List<WebElement> teamNamesEle;

    @FindBy(xpath = "//table//tbody//tr//td[2]")
    private List<WebElement> yearsEle;

    @FindBy(xpath = "//table//tbody//tr//td[6]")
    private List<WebElement> winPercentagesEle;

    @FindBy(xpath = "//a[@aria-label= 'Next']")
    private WebElement nextBtn ;

    //for oscars tab
    @FindBy(className = "year-link")
    private List<WebElement> yearsListEle;

    @FindBy(xpath = "//table//tbody//tr/td[1]")
    private List<WebElement> moviesNameElement;

    @FindBy(xpath = "//table//tbody//tr/td[2]")
    private List<WebElement> nominationElement;

    @FindBy(xpath = "//table//tbody//tr/td[3]")
    private List<WebElement> awardsElements;

    @FindBy(xpath = "//table//tbody//tr")
    private List<WebElement> rows;


    List<Map<String , Object>> movies ;



    
    public Wrappers(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);  
        this.movies = new ArrayList<>();  
       
    }

    public void navigateToUrl(){
        String url = "https://www.scrapethissite.com/pages/";
        driver.get(url);
        wait.until(ExpectedConditions.urlToBe("https://www.scrapethissite.com/pages/"));
    }

    public void clickOnPageTitle(String title) throws InterruptedException{
        
        try{
            List<WebElement> pageTitleEle = this.pageTitles;
            Thread.sleep(3000);
            wait.until(ExpectedConditions.visibilityOfAllElements(pageTitleEle));
            for(WebElement pageTitle : pageTitleEle){
                Thread.sleep(2000);
                if(pageTitle.getText().equalsIgnoreCase(title)){
                    System.out.println("...............clicking on link :: "+pageTitle.getText());
                    pageTitle.click();
                    Thread.sleep(1000);
                    break;
                }
            }
        }catch(Exception e){
            System.out.println("An error occured while clicking on given link");
            e.printStackTrace();
        }      
    }


   //for scraping table data
    public List<Map<String, Object>> getTableData(){
       
            List<Map<String , Object>> teamData = new ArrayList<>();
            try{
                for(int i =0 ; i<4; i++){
                    List<WebElement> teamNames = this.teamNamesEle;
                    List<WebElement> years = this.yearsEle;
                    List<WebElement> winPercents = this.winPercentagesEle;
        
                    for(int j = 0; j<teamNames.size();j++){
                        Double winpercent = Double.parseDouble(winPercents.get(j).getText().replace("%", "null"))/100;
                        if(winpercent<(0.40)){
                            Map <String , Object> data = new HashMap<>();
                            data.put("Epoch Time :: ", System.currentTimeMillis());
                            data.put("Team name :: ", teamNames.get(j).getText());
                            data.put("year :: ", years.get(j).getText());
                            data.put("win percentage ::", winPercents.get(j).getText());
                            teamData.add(data);
                        }
                    }
                    nextBtn.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
            }
        }catch(Exception e){
            System.out.println("Unable to get the table data ");
            e.printStackTrace();
        }
        return teamData;
    }

    public void scrapeMovieData() throws InterruptedException{
        try{
            List<WebElement> years = this.yearsListEle;
            wait.until(ExpectedConditions.visibilityOfAllElements(years));
            for(WebElement year : years){
                String yearText = year.getText();
                year.click();  
                Thread.sleep(3000); 
                getMovieData(yearText); 
                Thread.sleep(6000);
            }
        }catch(Exception e){
            System.out.println("An error occured while selecting year");
            e.printStackTrace();
        }    
    }

    public void getMovieData(String year) throws InterruptedException{
        Thread.sleep(6000);
        long epochTime = System.currentTimeMillis();
        List<WebElement> rowEle = this.rows;
        try{
            for(int i =0; i<=5;i++){
                    List <WebElement> moviesName = this.moviesNameElement;
                    List<WebElement> nominations = this.nominationElement;
                    List<WebElement> awards = this.awardsElements;
                    WebElement row = rowEle.get(i);
                    Boolean isWinner = false;

                    try {
                        // Locate the flag element within the current row
                        WebElement flagElement = row.findElement(By.xpath(".//td[4]/i"));
                        isWinner = flagElement.isDisplayed();
                    } catch (NoSuchElementException e) {
                        // Handle the case where the flag element is not found without logging an error
                        isWinner = false; // Set default value
                    } catch (Exception e) {
                        // Handle any other unexpected exceptions
                        System.out.println("An unexpected error occurred while processing row " + i + ": " + e.getMessage());
                    }

                    Map<String , Object> data = new HashMap<>();
                    data.put("Epoch Time :: ", System.currentTimeMillis());
                    data.put("Movie name ::", moviesName.get(i).getText());
                    data.put("nominations ::", nominations.get(i).getText());
                    data.put("awards:: ", awards.get(i).getText());
                    data.put("Is winner ", isWinner);

                    movies.add(data);
                }
            }catch(Exception e){
                    System.out.println("An error occured while scrapting the data for year ");
                    e.printStackTrace();
            }
    }


     
    public void writeDataToJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            File outputDir = new File("output");
            if(!outputDir.exists()){
                outputDir.mkdir();
            }
            objectMapper.writeValue(new File("output/oscar_winning_movies.json"), movies);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

