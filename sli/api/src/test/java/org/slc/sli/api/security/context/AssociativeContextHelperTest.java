package org.slc.sli.api.security.context;

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 5/7/12
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
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
