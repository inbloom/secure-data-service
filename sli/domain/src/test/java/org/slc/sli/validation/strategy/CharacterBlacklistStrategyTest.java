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
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for CharacterBlacklistStrategy
 * @author vmcglaughlin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class CharacterBlacklistStrategyTest {

    private static final String PREFIX = "some chars";
    private static final String SUFFIX = "and other chars";

    @Autowired
    private AbstractBlacklistStrategy characterBlacklistStrategy;

    @Test
    public void testSpringIsValid() {
        List<Character> springBadCharList = createSpringBadCharacterList();
        runTestLoop(springBadCharList, characterBlacklistStrategy, false);

        List<String> goodStringList = createGoodStringList();
        List<Character> goodCharList = createCharacterListFromStringList(goodStringList);
        runTestLoop(goodCharList, characterBlacklistStrategy, true);
    }

    @Test
    public void testIsValid() {
        AbstractBlacklistStrategy strategy = new CharacterBlacklistStrategy();
        List<String> badStringList = createBadStringList();
        List<Character> badCharList = createCharacterListFromStringList(badStringList);

        strategy.setInputCollection(badStringList);
        strategy.init();

        runTestLoop(badCharList, strategy, false);

        List<String> goodStringList = createGoodStringList();
        List<Character> goodCharList = createCharacterListFromStringList(goodStringList);

        runTestLoop(goodCharList, strategy, true);
    }

    private void runTestLoop(List<Character> inputList, AbstractBlacklistStrategy strategy, boolean shouldPass) {
        for (Character c : inputList) {
            String input = PREFIX + c + SUFFIX;
            boolean isValid = strategy.isValid("CharacterBlacklistStrategyTest", input);
            if (shouldPass) {
                assertTrue("Valid character did not pass validation: " + c, isValid);
            } else {
                assertFalse("Invalid character passed validation", isValid);
            }
        }
    }

    private List<Character> createSpringBadCharacterList() {
        List<Character> charList = new ArrayList<Character>();
        Collection<String> springInputCollection = characterBlacklistStrategy.getInputCollection();
        for (String entry : springInputCollection) {
            if (entry.isEmpty()) {
                continue;
            }

            if (!entry.startsWith("\\u")) {
                continue;
            }

            try {
                char c = (char) Integer.parseInt(entry.substring(2), 16);
                charList.add(new Character(c));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return charList;
    }

    private List<Character> createCharacterListFromStringList(List<String> stringList) {
        List<Character> charList = new ArrayList<Character>();

        for (String entry : stringList) {
            if (entry.isEmpty()) {
                continue;
            }

            try {
                String charStr = entry;
                if (entry.startsWith("\\u")) {
                    charStr = entry.substring(2);
                }

                char c = (char) Integer.parseInt(charStr, 16);
                charList.add(new Character(c));

            } catch (NumberFormatException e) {
                continue;
            }
        }

        return charList;
    }

    private List<String> createBadStringList() {
        List<String> stringList = new ArrayList<String>();

        stringList.add("\\u0000");
        stringList.add("\\u0001");
        stringList.add("\\u0002");
        stringList.add("\\u0003");
        stringList.add("\\u0004");
        stringList.add("\\u0005");
        stringList.add("\\u0006");
        stringList.add("\\u0007");
        stringList.add("\\u0008");
        stringList.add("\\u0009");
        stringList.add("\\u000A");
        stringList.add("\\u000B");
        stringList.add("\\u000C");
        stringList.add("\\u000D");
        stringList.add("\\u000E");
        stringList.add("\\u000F");
        stringList.add("\\u0010");
        stringList.add("\\u0011");
        stringList.add("\\u0012");
        stringList.add("\\u0013");
        stringList.add("\\u0014");
        stringList.add("\\u0015");
        stringList.add("\\u0016");
        stringList.add("\\u0017");
        stringList.add("\\u0018");
        stringList.add("\\u0019");
        stringList.add("\\u001A");
        stringList.add("\\u001B");
        stringList.add("\\u001C");
        stringList.add("\\u001D");
        stringList.add("\\u001E");
        stringList.add("\\u001F");
        stringList.add("\\u007F");
        stringList.add("\\u003C");
        stringList.add("\\u003E");

        // single character test cases
        stringList.add("!");
        stringList.add("_");
        stringList.add("~");
        return stringList;
    }

    private List<String> createGoodStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("\\u0020");
        stringList.add("\\u0021");
        stringList.add("\\u0022");
        stringList.add("\\u0023");
        stringList.add("\\u0024");
        stringList.add("\\u0025");
        stringList.add("\\u0026");
        stringList.add("\\u0027");
        stringList.add("\\u0028");
        stringList.add("\\u0029");
        stringList.add("\\u002A");
        stringList.add("\\u002B");
        stringList.add("\\u002C");
        stringList.add("\\u002D");
        stringList.add("\\u002E");
        stringList.add("\\u002F");
        stringList.add("\\u0030");
        stringList.add("\\u0031");
        stringList.add("\\u0032");
        stringList.add("\\u0033");
        stringList.add("\\u0034");
        stringList.add("\\u0035");
        stringList.add("\\u0036");
        stringList.add("\\u0037");
        stringList.add("\\u0038");
        stringList.add("\\u0039");
        stringList.add("\\u003A");
        stringList.add("\\u003B");
        stringList.add("\\u003D");
        stringList.add("\\u003F");
        stringList.add("\\u0040");
        stringList.add("\\u0041");
        stringList.add("\\u0042");
        stringList.add("\\u0043");
        stringList.add("\\u0044");
        stringList.add("\\u0045");
        stringList.add("\\u0046");
        stringList.add("\\u0047");
        stringList.add("\\u0048");
        stringList.add("\\u0049");
        stringList.add("\\u004A");
        stringList.add("\\u004B");
        stringList.add("\\u004C");
        stringList.add("\\u004D");
        stringList.add("\\u004E");
        stringList.add("\\u004F");
        stringList.add("\\u0050");
        stringList.add("\\u0051");
        stringList.add("\\u0052");
        stringList.add("\\u0053");
        stringList.add("\\u0054");
        stringList.add("\\u0055");
        stringList.add("\\u0056");
        stringList.add("\\u0057");
        stringList.add("\\u0058");
        stringList.add("\\u0059");
        stringList.add("\\u005A");
        stringList.add("\\u005B");
        stringList.add("\\u005C");
        stringList.add("\\u005D");
        stringList.add("\\u005E");
        stringList.add("\\u005F");
        stringList.add("\\u0060");
        stringList.add("\\u0061");
        stringList.add("\\u0062");
        stringList.add("\\u0063");
        stringList.add("\\u0064");
        stringList.add("\\u0065");
        stringList.add("\\u0066");
        stringList.add("\\u0067");
        stringList.add("\\u0068");
        stringList.add("\\u0069");
        stringList.add("\\u006A");
        stringList.add("\\u006B");
        stringList.add("\\u006C");
        stringList.add("\\u006D");
        stringList.add("\\u006E");
        stringList.add("\\u006F");
        stringList.add("\\u0070");
        stringList.add("\\u0071");
        stringList.add("\\u0072");
        stringList.add("\\u0073");
        stringList.add("\\u0074");
        stringList.add("\\u0075");
        stringList.add("\\u0076");
        stringList.add("\\u0077");
        stringList.add("\\u0078");
        stringList.add("\\u0079");
        stringList.add("\\u007A");
        stringList.add("\\u007B");
        stringList.add("\\u007C");
        stringList.add("\\u007D");
        stringList.add("\\u007E");

        return stringList;
    }

}
