package org.slc.sli.api.security.context;

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Tests
 *
 */
public class AssociativeContextHelperTest {

    private AssociativeContextHelper helper = new AssociativeContextHelper(); //class under test

    @Test
    public void testGetFilterDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 3);

        assertEquals("Should match", "2012-02-03", helper.getFilterDate("90", calendar));
        calendar.clear();
        calendar.set(2012, 4, 3);
        assertEquals("Should match", "2012-05-03", helper.getFilterDate("", calendar));
        calendar.clear();
        calendar.set(2012, 1, 3);
        assertEquals("Should match", "2012-02-03", helper.getFilterDate(null, calendar));
    }
}
