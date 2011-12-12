package org.slc.sli.controller.selenium;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class StudentListSeleniumITest {

    WebDriver driver;
    String studentListUrl;
    
    @Before
    public void setup() {
        driver = new FirefoxDriver();
        studentListUrl = "http://localhost:8080/dashboard/studentlist?username=sravan";
    }
    
    @Test
    public void testStudentListPage() {
        driver.get(studentListUrl);
        Select schoolSelect = new Select(driver.findElement(By.id("schoolSelect")));
        schoolSelect.selectByVisibleText("Illinois PS145");
        Select courseSelect = new Select(driver.findElement(By.id("courseSelect")));
        courseSelect.selectByVisibleText("Algebra");
        Select sectionSelect = new Select(driver.findElement(By.id("sectionSelect")));
        sectionSelect.selectByVisibleText("Section1");
       
        WebElement temp = driver.findElement(By.id("studentDiv"));
        String studentText = temp.getText();
        
        assertTrue(studentText.contains("Dawson Deborah"));
        assertFalse(studentText.contains("Random name"));
        driver.close();
    }
    
    
}
