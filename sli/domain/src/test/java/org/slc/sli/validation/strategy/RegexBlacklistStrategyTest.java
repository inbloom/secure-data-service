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

    @Autowired
    AbstractBlacklistStrategy regexBlacklistStrategy;

    @Test
    public void testGetValid() {
        String prefix = "some strings";
        String suffix = "and other strings";


        List<String> badStringList = createBadStringList();
        for (String s : badStringList) {
            String input = prefix + s + suffix;
            assertFalse(s + " should not be a valid string", regexBlacklistStrategy.isValid("RegexBlacklistStrategyTest", input));
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = prefix + s + suffix;
            assertTrue(s + " should be a valid string", regexBlacklistStrategy.isValid("RegexBlacklistStrategyTest", input));
        }
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