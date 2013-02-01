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


package org.slc.sli.validation.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for RegexBlacklistStrategy
 * @author vmcglaughlin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RegexBlacklistStrategyTest {

    private static final String PREFIX = "some strings";
    private static final String SUFFIX = "and other strings";

    @Autowired
    private AbstractBlacklistStrategy regexBlacklistStrategy;

    @Test
    public void testSpringIsValid() {
        List<String> badStringList = createBadStringList();
        runTestLoop(badStringList, regexBlacklistStrategy, false);

        List<String> goodStringList = createGoodStringList();
        runTestLoop(goodStringList, regexBlacklistStrategy, true);
    }

    @Test
    public void testIsValid() {
        AbstractBlacklistStrategy strategy = new RegexBlacklistStrategy();
        List<String> badRegexStringList = createBadRegexStringList();

        strategy.setInputCollection(badRegexStringList);
        strategy.init();

        List<String> badStringList = createBadStringList();
        runTestLoop(badStringList, strategy, false);

        List<String> goodStringList = createGoodStringList();
        runTestLoop(goodStringList, strategy, true);
    }

    private void runTestLoop(List<String> inputList, AbstractBlacklistStrategy strategy, boolean shouldPass) {
        for (String s : inputList) {
            String input = PREFIX + s + SUFFIX;
            boolean isValid = strategy.isValid("CharacterBlacklistStrategyTest", input);
            if (shouldPass) {
                assertTrue("Valid string did not pass validation: " + s, isValid);
            } else {
                assertFalse("Invalid string passed validation: " + s, isValid);
            }
        }
    }

    private List<String> createBadRegexStringList() {
        List<String> regexStringList = new ArrayList<String>();
        regexStringList.add("\\d{8}");
        regexStringList.add("[A-Za-z0-9._%'-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}");
        regexStringList.add("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        return regexStringList;
    }

    private List<String> createBadStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("12345678");
        stringList.add("sample@gmail.com");
        stringList.add("sample_123@test.net");
        stringList.add("weird'-%address@abc123.us");
        stringList.add("0.0.0.0");
        stringList.add("123.123.123.123");
        stringList.add("192.168.1.1");
        return stringList;
    }

    private List<String> createGoodStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("1234567");
        stringList.add("invalid#email#@test.com");
        stringList.add("1234.1234.1.23");
        return stringList;
    }
}
