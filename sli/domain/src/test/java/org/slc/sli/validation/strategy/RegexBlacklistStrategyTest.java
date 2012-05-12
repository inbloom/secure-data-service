package org.slc.sli.validation.strategy;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.esapi.errors.ValidationException;
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
    public void testSpringGetValid() {
        List<String> badStringList = createBadStringList();
        for (String s : badStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                regexBlacklistStrategy.getValid("RegexBlacklistStrategyTest", input);
                fail("Invalid string passed validation: " + s);
            } catch (ValidationException e) {
                continue;
            }
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                regexBlacklistStrategy.getValid("RegexBlacklistStrategyTest", input);
            } catch (ValidationException e) {
                fail("Valid string did not pass validation" + s);
            }
        }
    }

    @Test
    public void testGetValid() {
        AbstractBlacklistStrategy strategy = new RegexBlacklistStrategy();
        List<String> badRegexStringList = createBadRegexStringList();

        strategy.setInputCollection(badRegexStringList);
        strategy.init();

        List<String> badStringList = createBadStringList();
        for (String s : badStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                strategy.getValid("RegexBlacklistStrategyTest", input);
                fail("Invalid string passed validation: " + s);
            } catch (ValidationException e) {
                continue;
            }
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                strategy.getValid("RegexBlacklistStrategyTest", input);
            } catch (ValidationException e) {
                fail("Valid string did not pass validation" + s);
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