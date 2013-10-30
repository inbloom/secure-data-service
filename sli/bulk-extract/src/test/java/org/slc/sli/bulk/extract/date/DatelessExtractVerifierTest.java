package org.slc.sli.bulk.extract.date;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

public class DatelessExtractVerifierTest {
    private DatelessExtractVerifier datelessExtractVerifier = new DatelessExtractVerifier();

    @Test
    public void testshouldExtract() {
        Entity student = Mockito.mock(Entity.class);
        Mockito.when(student.getType()).thenReturn(EntityNames.STUDENT);

        Assert.assertTrue(datelessExtractVerifier.shouldExtract(student, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(datelessExtractVerifier.shouldExtract(student, null));
    }

}
