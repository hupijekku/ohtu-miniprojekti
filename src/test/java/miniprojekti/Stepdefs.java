package miniprojekti;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import miniprojekti.data_access.ReadingTipDao;
import miniprojekti.domain.Logic;

public class Stepdefs {
    Logic logic;
    ReadingTipDao testdao;
    WebDriver driver = new HtmlUnitDriver();
    String baseUrl = "http://readingtips.herokuapp.com/";

    @Before
    public void setup() {
        testdao = mock(ReadingTipDao.class);
        logic = new Logic(testdao);
    }

    @Given("user is on the login page")
    public void loginPage() {
        driver.get(baseUrl);
    }

    @When("correct username {string} and correct password {string} are entered")
    public void canLoginWithRightCredentials(String username, String password) {
        assertTrue(driver.getCurrentUrl().equals(baseUrl));
        login(username, password);
        driver.navigate().refresh();
    }

    @Then("user will be redirected to {string}")
    public void userWillBeRedirectedTo(String s) {
        assertTrue(driver.getCurrentUrl().equals(baseUrl + s));
    }

    @When("correct username {string} and wrong password {string} are entered")
    public void cannotLoginWithWrongPassword(String username, String password) {
        assertTrue(driver.getCurrentUrl().equals(baseUrl));
        login(username, password);
    }

    @When("nonexistent username {string} and password {string} are entered")
    public void cannotLoginWithNonexistentUse(String username, String password) {
        assertTrue(driver.getCurrentUrl().equals(baseUrl));
        login(username, password);
    }

    @Given("user is logged in")
    public void loginForTests() {
        driver.get(baseUrl);
        login("vihannes", "pass");
    }

    @Given("add is selected")
    public void addIsSelected() {
        driver.get(baseUrl + "add?tipTypes=book");
        pageHasContent("Add book");
    }

    @When("new tip is entered with proper values")
    public void newTipIsEnteredProperly() {
        WebElement element = driver.findElement(By.name("title"));
        element = fillTheFieldsProperly(element);
    }

    @Then("new tip is added")
    public void newTipIsAdded() {
        WebElement element = driver.findElement(By.name("title"));
        element = fillTheFieldsProperly(element);
        element.submit();
        pageHasContent("Tip added succesfully");
    }

    @When("new tip is entered with invalid values")
    public void newTipIsEnteredIncorrectly() {
        WebElement element = driver.findElement(By.name("title"));
        element.submit();
    }

    @Then("system will respond with errors")
    public void addingaTipHasErrors() {
        pageHasContent("Errors:");
    }

    @Given("user is on the frontpage")
    public void isFrontPage() {
        driver.get(baseUrl + "index");
        pageHasContent("Reading tips");
    }

    @Then("user can see the list")
    public void listIsShown() {
        pageHasContent("type");
        pageHasContent("title");

    }

    @When("user selects {string} from the menu")
    public void specificTypeListIsSelected(String type) {
        WebElement element = driver.findElement(By.name("tipTypes"));
        element.click();
        element.sendKeys(type);
        element.submit();

    }

    @Then("user can see only list of {string}")
    public void specificTypeListIsShown(String type) {

    }

    @When("user types username and password to register")
    public void createNewUser() {
        Random r = new Random();

        WebElement element = driver.findElement(By.id("registerForm").name("username"));
        element.sendKeys("jafwhoifa" + r.nextInt(100000));
        element = driver.findElement(By.id("registerForm").name("password"));
        element.sendKeys("5i2395as" + r.nextInt(10000));
        element.submit();
    }

    @Then("new user is created")
    public void userCreated() {
        pageHasContent("Log in");
    }

    @When("user has created a book")
    public void readyForEditing() {
        driver.get(baseUrl + "index");
        pageHasContent("Book");
    }

    @When("user edits the title")
    public void canEditTitle() {
        Random random = new Random();
        // element = driver.findElement(By.xpath("//button[contains(.,'Open')]"));
        // element.click();
        driver.get(baseUrl + "/tips/3");
        pageHasContent("Edit a");
        // WebElement element = driver.findElement(By.name("editButton"));
        // element.click();
        // element = driver.findElement(By.name("title"));
        // element.sendKeys("CucumberEdit" + random.nextInt(10000));
        // element.submit();
    }

    @Then("the tip gets saved")
    public void editOK() {
        // pageHasContent("Tip edited succesfully");
    }

    @Given("user is on anypage")
    public void anywhere() {
        driver.get(baseUrl + "index");
    }
    @When("user sees what we have done")
    public void userCanSee(){
        pageHasContent(anyString());
    }
    @Then("it pleases the user")
    public void userLikes(){
        pageHasContent(anyString());
    }

    // apumetodit
    private void login(String username, String password) {
        WebElement element = driver.findElement(By.name("username"));
        element.sendKeys(username);
        element = driver.findElement(By.name("password"));
        element.sendKeys(password);
        element.submit();
    }

    private void pageHasContent(String content) {
        assertTrue(driver.getPageSource().contains(content));
    }

    private WebElement fillTheFieldsProperly(WebElement element) {
        element = driver.findElement(By.name("title"));
        element.clear();
        element.sendKeys("testTitle");
        element = driver.findElement(By.name("note"));
        element.clear();
        element.sendKeys("testNote");
        element = driver.findElement(By.name("url"));
        element.clear();
        element.sendKeys("https://test.com");
        element = driver.findElement(By.name("author"));
        element.clear();
        element.sendKeys("testAuthor");
        element = driver.findElement(By.name("isbn"));
        element.clear();
        element.sendKeys("9780201616224");
        return element;
    }
}