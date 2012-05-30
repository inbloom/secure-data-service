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
 * JUnits for StringBlacklistStrategy
 * @author vmcglaughlin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StringBlacklistStrategyTest {

    // trailing and leading spaces required to match word boundaries
    private static final String PREFIX = "some chars ";
    private static final String SUFFIX = " and other chars";

    @Autowired
    private AbstractBlacklistStrategy stringBlacklistStrategy;
    
    @Autowired
    private AbstractBlacklistStrategy relaxedStringBlacklistStrategy;

    @Test
    public void testBlacklisting() {
        List<String> badStringList = createBadRelaxedStringList();
        runTestLoop(badStringList, relaxedStringBlacklistStrategy, false);

        List<String> goodStringList = createGoodStringList();
        runTestLoop(goodStringList, relaxedStringBlacklistStrategy, true);
    }
    
    @Test
    public void testRelaxedBlacklisting() {
        List<String> badStringList = createBadStringList();
        runTestLoop(badStringList, stringBlacklistStrategy, false);

        List<String> goodStringList = createGoodStringList();
        runTestLoop(goodStringList, stringBlacklistStrategy, true);
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

    private List<String> createBadStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("script");
        stringList.add("img");
        stringList.add("src");
        return stringList;
    }
    
    private List<String> createBadRelaxedStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("<script");
        stringList.add("<iframe");
        stringList.add("<frame");
        return stringList;
    }

    private List<String> createGoodStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("innocuous");
        stringList.add("list");
        stringList.add("of");
        stringList.add("strings");
        return stringList;
    }

}