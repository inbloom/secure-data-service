package org.slc.sli.api.selectors.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SelectorParseExceptionTest {

    @Test(expected = SelectorParseException.class)
    public void testCanThrow() throws SelectorParseException {
        throw new SelectorParseException("some message");
    }
}
