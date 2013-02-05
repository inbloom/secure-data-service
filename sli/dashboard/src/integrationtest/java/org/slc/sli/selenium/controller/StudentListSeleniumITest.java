/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

/**
 * TODO: Write Javadoc
 */
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
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
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                driver.close();
            }
    }
}
