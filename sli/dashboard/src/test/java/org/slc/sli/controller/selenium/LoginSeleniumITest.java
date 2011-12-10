package org.slc.sli.controller.selenium;

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
 
    @Before
    public void setup() {
        driver = new FirefoxDriver();
        loginUrl = "http://localhost:8080/dashboard/login";
        testUser = "sravan";
    }
    
    @Test
    public void testLoginPage() {
        driver.get(loginUrl);
        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys(testUser);
        
        WebElement loginForm = driver.findElement(By.name("loginForm"));
        loginForm.submit();
    }
    
}
