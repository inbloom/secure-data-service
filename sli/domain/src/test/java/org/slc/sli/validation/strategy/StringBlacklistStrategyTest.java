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
 * JUnits for StringBlacklistStrategy
 * @author vmcglaughlin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StringBlacklistStrategyTest {

    @Autowired
    AbstractBlacklistStrategy stringBlacklistStrategy;

    @Test
    public void testGetValid() {
        // trailing and leading spaces required to match word boundaries
        String prefix = "some strings ";
        String suffix = " and other strings";

        List<String> springBadStringList = createSpringBadStringList();
        for (String s : springBadStringList) {
            String input = prefix + s + suffix;
            assertFalse(s + " should not be a valid string", stringBlacklistStrategy.isValid("StringBlacklistStrategyTest", input));
        }

        List<String> badStringList = createBadStringList();
        for (String s : badStringList) {
            String input = prefix + s + suffix;
            assertFalse(s + " should not be a valid string", stringBlacklistStrategy.isValid("StringBlacklistStrategyTest", input));
        }

        List<String> goodStringList = createGoodStringList();
        for (String s : goodStringList) {
            String input = prefix + s + suffix;
            assertTrue(s + " should be a valid string", stringBlacklistStrategy.isValid("StringBlacklistStrategyTest", input));
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