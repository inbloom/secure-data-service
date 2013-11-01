package org.slc.sli.bulk.extract.date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

public class TeacherSchoolAssociationExtractVerifierTest {

    @Mock
    private EdOrgExtractHelper edOrgExtractHelper;

    @InjectMocks
    private TeacherSchoolAssociationExtractVerifier underTest = new TeacherSchoolAssociationExtractVerifier();

    private TeacherSchoolAssociationExtractVerifier tsaev;

    @Before
    public void Init() {
        MockitoAnnotations.initMocks(this);

        tsaev = Mockito.spy(underTest);
        Mockito.doReturn(new SimpleExtractVerifier()).when(tsaev).getExtractVerifer();
    }

    @Test
    public void testSholdExtractEntities() {
        List<Entity> seaos = new ArrayList<Entity>();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2001-05-24");
        Entity entity1 = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Map<String, Object> body2 = new HashMap<String, Object>();
        body2.put(ParameterConstants.BEGIN_DATE, "2009-01-01");
        Entity entity2 = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        seaos.add(entity1);
        seaos.add(entity2);

        seaos.add(createSEOA("2002-01-01", "2003-01-01"));
        seaos.add(createSEOA("2005-01-01", "2008-01-01"));

        Assert.assertFalse(tsaev.shouldExtract(seaos, DateTime.parse("2001-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(tsaev.shouldExtract(seaos, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(tsaev.shouldExtract(seaos, DateTime.parse("2006-05-23", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testshouldExtractTeacherSchoolAssociation() {
        Entity mSeoa = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2009-09-02");
        body.put(ParameterConstants.END_DATE, "2010-05-31");
        Mockito.when(mSeoa.getBody()).thenReturn(body);
        Mockito.when(mSeoa.getType()).thenReturn(EntityNames.STAFF_ED_ORG_ASSOCIATION);

        Mockito.when(edOrgExtractHelper.retrieveSEOAS(Matchers.eq("Staff1"), Matchers.eq("LEA"))).thenReturn(Arrays.asList(mSeoa));

        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, "Staff1");
        tsaBody.put(ParameterConstants.SCHOOL_ID, "LEA");
        Entity tsa = Mockito.mock(Entity.class);
        Mockito.when(tsa.getBody()).thenReturn(tsaBody);
        Mockito.when(tsa.getType()).thenReturn(EntityNames.TEACHER_SCHOOL_ASSOCIATION);

        Assert.assertTrue(tsaev.shouldExtract(tsa, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(tsaev.shouldExtract(tsa, DateTime.parse("2009-09-02", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(tsaev.shouldExtract(tsa, null));
        Assert.assertFalse(tsaev.shouldExtract(tsa, DateTime.parse("2009-09-01", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(tsaev.shouldExtract(tsa, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat())));
    }


    private Entity createSEOA(String beginDate, String endDate) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, beginDate);
        if(endDate != null) {
            body.put(ParameterConstants.END_DATE, endDate);
        }
        Entity entity = new MongoEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        return entity;
    }

}
