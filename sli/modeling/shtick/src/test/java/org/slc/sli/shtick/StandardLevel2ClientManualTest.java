package org.slc.sli.shtick;

import org.junit.Before;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManualTest {
    private Level2ClientManual client; //class under test

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";

    @Before
    public void setup() {
        client = new StandardLevel2ClientManual(BASE_URL);
    }
}
