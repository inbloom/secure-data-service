package org.slc.sli.selenium.controller;

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
    String usernameParamPrefix;
    String userA;
    String userB;
    
    @Before
    public void setup() {
        driver = new FirefoxDriver();
        studentListUrl = "http://localhost:8080/dashboard/studentlist";
        usernameParamPrefix = "?username=";
        userA = "sravan";
        userB = "cgray";
    }
    
    @Test
    public void testStudentListPage() {
    	try {
	        driver.get(studentListUrl + usernameParamPrefix + userA);
	        Select schoolSelect = new Select(driver.findElement(By.id("schoolSelect")));
	        schoolSelect.selectByVisibleText("Illinois PS145");
	        Select courseSelect = new Select(driver.findElement(By.id("courseSelect")));
	        courseSelect.selectByVisibleText("Algebra");
	        
	        // Verifying section 1 content
	        Select sectionSelect = new Select(driver.findElement(By.id("sectionSelect")));
	        sectionSelect.selectByVisibleText("Section1");
	       
	        WebElement textDiv = driver.findElement(By.id("studentDiv"));
	        String studentText = textDiv.getText();
	        
	        assertTrue(studentText.contains("Dawson Deborah"));
	        assertFalse(studentText.contains("Random name"));
	        
	        // Verifying section 2
	        sectionSelect = new Select(driver.findElement(By.id("sectionSelect")));
	        sectionSelect.selectByVisibleText("Section2");
	       
	        WebElement temp = driver.findElement(By.id("studentDiv"));
	        studentText = temp.getText();
	        
	        assertTrue(studentText.contains("Marks Hector"));
	        assertFalse(studentText.contains("Dawson Deborah"));
    	}
    	catch (Exception e){
    		System.err.println(e.getMessage());
    	}
        finally {
        	driver.close();
        }
    }   

    @Test
    public void testUsersHaveDiffLists() {
    	try {
	        driver.get(studentListUrl + usernameParamPrefix + userA);
	        Select userASchoolSelect = new Select(driver.findElement(By.id("schoolSelect")));
	        String userASchoolItem = userASchoolSelect.getOptions().get(1).getText();
	        
	        driver.get(studentListUrl + usernameParamPrefix + userB);
	        Select userBSchoolSelect = new Select(driver.findElement(By.id("schoolSelect")));
	        String userBSchoolItem = userBSchoolSelect.getOptions().get(1).getText();
	
	        assertFalse(userBSchoolItem.equals(userASchoolItem));
    	}
    	catch (Exception e){
    		System.err.println(e.getMessage());
    	}
        finally {
        	driver.close();
        }
    }
}