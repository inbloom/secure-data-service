package org.slc.sli.selenium.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginSeleniumITest {

    WebDriver driver;
    String loginUrl;
    String testUser;
    String testBadUser;
 
    @Before
    public void setup() {
        driver = new FirefoxDriver();
        loginUrl = "http://localhost:8080/dashboard/login";
        testUser = "sravan";
        testBadUser = "lululu";
    }
    
    @Test
    public void testLoginPage() {
        driver.get(loginUrl);
        
        /*
         * Test for invalid username
         */
        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys(testBadUser);
        // Before submission - error message should not be displayed
        WebElement errorMessage = driver.findElement(By.name("errorMessage"));
        assertTrue(errorMessage.getCssValue("display").equalsIgnoreCase("none"));

        WebElement loginForm = driver.findElement(By.name("loginForm"));
        loginForm.submit();
        // After submission - error message should be displayed
        errorMessage = driver.findElement(By.name("errorMessage"));
        assertTrue(errorMessage.getCssValue("display").equalsIgnoreCase("block"));

        /*
         * Test for a valid username
         */
        username = driver.findElement(By.name("username"));
        username.sendKeys(testUser);
        
        loginForm = driver.findElement(By.name("loginForm"));
        loginForm.submit();
        WebElement body = driver.findElement(By.tagName("body"));
        String bodyText = body.getText();
        assertTrue(bodyText.contains("Select an application"));
        driver.close();
    }
    
}
