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

    @Autowired
    AbstractBlacklistStrategy characterBlacklistStrategy;

    @Test
    public void testGetValid() {
        String prefix = "some chars";
        String suffix = "and other chars";

        List<Character> springBadCharList = createSpringBadCharacterList();
        for (Character c : springBadCharList) {
            String input = prefix + c + suffix;
            assertFalse(characterBlacklistStrategy.isValid("CharacterBlacklistStrategyTest", input));
        }

        List<Character> badCharList = createBadCharacterList();
        for (Character c : badCharList) {
            String input = prefix + c + suffix;
            assertFalse(characterBlacklistStrategy.isValid("CharacterBlacklistStrategyTest", input));
        }

        List<Character> goodCharList = createGoodCharacterList();
        for (Character c : goodCharList) {
            String input = prefix + c + suffix;
            assertTrue(c + " should be a valid character", characterBlacklistStrategy.isValid("CharacterBlacklistStrategyTest", input));
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

    private List<Character> createBadCharacterList() {
        List<Character> charList = new ArrayList<Character>();
        charList.add(new Character('\u0000'));
        charList.add(new Character('\u0001'));
        charList.add(new Character('\u0002'));
        charList.add(new Character('\u0003'));
        charList.add(new Character('\u0004'));
        charList.add(new Character('\u0005'));
        charList.add(new Character('\u0006'));
        charList.add(new Character('\u0007'));
        charList.add(new Character('\u0008'));
        charList.add(new Character('\u0009'));
        // can't do \u000A
        charList.add(new Character('\u000B'));
        charList.add(new Character('\u000C'));
        // can't do \u000D
        charList.add(new Character('\u000E'));
        charList.add(new Character('\u000F'));
        charList.add(new Character('\u0010'));
        charList.add(new Character('\u0011'));
        charList.add(new Character('\u0012'));
        charList.add(new Character('\u0013'));
        charList.add(new Character('\u0014'));
        charList.add(new Character('\u0015'));
        charList.add(new Character('\u0016'));
        charList.add(new Character('\u0017'));
        charList.add(new Character('\u0018'));
        charList.add(new Character('\u0019'));
        charList.add(new Character('\u001A'));
        charList.add(new Character('\u001B'));
        charList.add(new Character('\u001C'));
        charList.add(new Character('\u001D'));
        charList.add(new Character('\u001E'));
        charList.add(new Character('\u001F'));
        charList.add(new Character('\u007F'));
        charList.add(new Character('\u003C'));
        charList.add(new Character('\u003E'));

        return charList;
    }

    private List<Character> createGoodCharacterList() {
        List<Character> charList = new ArrayList<Character>();
        charList.add(new Character('\u0020'));
        charList.add(new Character('\u0021'));
        charList.add(new Character('\u0022'));
        charList.add(new Character('\u0023'));
        charList.add(new Character('\u0024'));
        charList.add(new Character('\u0025'));
        charList.add(new Character('\u0026'));
        // can't do \u0027
        charList.add(new Character('\u0028'));
        charList.add(new Character('\u0029'));
        charList.add(new Character('\u002A'));
        charList.add(new Character('\u002B'));
        charList.add(new Character('\u002C'));
        charList.add(new Character('\u002D'));
        charList.add(new Character('\u002E'));
        charList.add(new Character('\u002F'));
        charList.add(new Character('\u0030'));
        charList.add(new Character('\u0031'));
        charList.add(new Character('\u0032'));
        charList.add(new Character('\u0033'));
        charList.add(new Character('\u0034'));
        charList.add(new Character('\u0035'));
        charList.add(new Character('\u0036'));
        charList.add(new Character('\u0037'));
        charList.add(new Character('\u0038'));
        charList.add(new Character('\u0039'));
        charList.add(new Character('\u003A'));
        charList.add(new Character('\u003B'));
        charList.add(new Character('\u003D'));
        charList.add(new Character('\u003F'));
        charList.add(new Character('\u0040'));
        charList.add(new Character('\u0041'));
        charList.add(new Character('\u0042'));
        charList.add(new Character('\u0043'));
        charList.add(new Character('\u0044'));
        charList.add(new Character('\u0045'));
        charList.add(new Character('\u0046'));
        charList.add(new Character('\u0047'));
        charList.add(new Character('\u0048'));
        charList.add(new Character('\u0049'));
        charList.add(new Character('\u004A'));
        charList.add(new Character('\u004B'));
        charList.add(new Character('\u004C'));
        charList.add(new Character('\u004D'));
        charList.add(new Character('\u004E'));
        charList.add(new Character('\u004F'));
        charList.add(new Character('\u0050'));
        charList.add(new Character('\u0051'));
        charList.add(new Character('\u0052'));
        charList.add(new Character('\u0053'));
        charList.add(new Character('\u0054'));
        charList.add(new Character('\u0055'));
        charList.add(new Character('\u0056'));
        charList.add(new Character('\u0057'));
        charList.add(new Character('\u0058'));
        charList.add(new Character('\u0059'));
        charList.add(new Character('\u005A'));
        charList.add(new Character('\u005B'));
        // can't do \u005C
        charList.add(new Character('\u005D'));
        charList.add(new Character('\u005E'));
        charList.add(new Character('\u005F'));
        charList.add(new Character('\u0060'));
        charList.add(new Character('\u0061'));
        charList.add(new Character('\u0062'));
        charList.add(new Character('\u0063'));
        charList.add(new Character('\u0064'));
        charList.add(new Character('\u0065'));
        charList.add(new Character('\u0066'));
        charList.add(new Character('\u0067'));
        charList.add(new Character('\u0068'));
        charList.add(new Character('\u0069'));
        charList.add(new Character('\u006A'));
        charList.add(new Character('\u006B'));
        charList.add(new Character('\u006C'));
        charList.add(new Character('\u006D'));
        charList.add(new Character('\u006E'));
        charList.add(new Character('\u006F'));
        charList.add(new Character('\u0070'));
        charList.add(new Character('\u0071'));
        charList.add(new Character('\u0072'));
        charList.add(new Character('\u0073'));
        charList.add(new Character('\u0074'));
        charList.add(new Character('\u0075'));
        charList.add(new Character('\u0076'));
        charList.add(new Character('\u0077'));
        charList.add(new Character('\u0078'));
        charList.add(new Character('\u0079'));
        charList.add(new Character('\u007A'));
        charList.add(new Character('\u007B'));
        charList.add(new Character('\u007C'));
        charList.add(new Character('\u007D'));
        charList.add(new Character('\u007E'));

        return charList;
    }

}