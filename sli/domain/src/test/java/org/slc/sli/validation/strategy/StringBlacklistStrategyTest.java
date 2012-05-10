package org.slc.sli.validation.strategy;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.esapi.errors.ValidationException;
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
    private final String PREFIX = "some chars ";
    private final String SUFFIX = " and other chars";

    @Autowired
    private AbstractBlacklistStrategy stringBlacklistStrategy;

    @Test
    public void testSpringGetValid() {
        List<String> springBadStringList = createSpringBadStringList();
        for (String s : springBadStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                stringBlacklistStrategy.getValid("StringBlacklistStrategyTest", input);
                fail("Invalid string passed validation: " + s);
            } catch (ValidationException e) {
                continue;
            }
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                stringBlacklistStrategy.getValid("StringBlacklistStrategyTest", input);
            } catch (ValidationException e) {
                fail("Valid string did not pass validation: " + s);
            }
        }
    }

    @Test
    public void testGetValid() {
        AbstractBlacklistStrategy strategy = new StringBlacklistStrategy();
        List<String> badStringList = createBadStringList();

        strategy.setInputCollection(badStringList);
        strategy.init();

        for (String s : badStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                strategy.getValid("StringBlacklistStrategyTest", input);
                fail("Invalid string passed validation: " + s);
            } catch (ValidationException e) {
                continue;
            }
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = PREFIX + s + SUFFIX;
            try {
                strategy.getValid("StringBlacklistStrategyTest", input);
            } catch (ValidationException e) {
                fail("Valid string did not pass validation: " + s);
            }
        }
    }

    private List<String> createSpringBadStringList() {
        Collection<String> inputCollection = stringBlacklistStrategy.getInputCollection();
        return new ArrayList<String>(inputCollection);
    }

    private List<String> createBadStringList() {
        List<String> stringList = new ArrayList<String>();
        stringList.add("script");
        stringList.add("img");
        stringList.add("src");
        stringList.add("onload");
        stringList.add("onunload");
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