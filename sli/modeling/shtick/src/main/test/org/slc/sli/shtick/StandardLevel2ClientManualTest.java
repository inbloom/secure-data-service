package org.slc.sli.shtick;

import org.junit.Before;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManualTest {
    private Level2ClientManual client; //class under test

    @Before
    public void setup() {
        client = new StandardLevel2ClientManual(new StandardLevel1Client(new StandardLevel0Client()));
    }
}
