package org.slc.sli.bulk.extract.context.resolver.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.domain.Entity;

/**
 * Unit test for the student context resolver
 * 
 * @author nbrown
 * 
 */
public class StudentContextResolverTest {
    private final StudentContextResolver underTest = new StudentContextResolver();
    
    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "42");
        DateTime today = DateTime.now();
        List<String> badEdorgs = Arrays.asList("badEdOrg");
        DateTimeFormatter format = ISODateTimeFormat.date();
        Map<String, Object> oldSchool = new HashMap<String, Object>();
        oldSchool.put("_id", "oldSchool");
        oldSchool.put("entryDate", today.minusMonths(15).toString(format));
        oldSchool.put("exitWithdrawDate", today.minusMonths(3).toString(format));
        oldSchool.put("edOrgs", badEdorgs);
        Map<String, Object> currentSchool = new HashMap<String, Object>();
        currentSchool.put("_id", "currentSchool");
        currentSchool.put("entryDate", today.minusMonths(3).toString(format));
        currentSchool.put("exitWithdrawDate", today.plusMonths(9).toString(format));
        currentSchool.put("edOrgs", Arrays.asList("edOrg1", "edOrg2"));
        Map<String, Object> futureSchool = new HashMap<String, Object>();
        futureSchool.put("_id", "futureSchool");
        futureSchool.put("entryDate", today.plusMonths(9).toString(format));
        futureSchool.put("exitWithdrawDate", today.plusMonths(21).toString(format));
        futureSchool.put("edOrgs", badEdorgs);
        Map<String, Object> unboundedSchool = new HashMap<String, Object>();
        unboundedSchool.put("_id", "futureSchool");
        unboundedSchool.put("entryDate", today.minusMonths(15).toString(format));
        unboundedSchool.put("edOrgs", badEdorgs);
        unboundedSchool.put("edOrgs", Arrays.asList("edOrg1", "edOrg3"));
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String,Object>>>();
        denormalized.put("schools", Arrays.asList(oldSchool, currentSchool, futureSchool, unboundedSchool));
        Entity testStudent = Mockito.mock(Entity.class);
        when(testStudent.getEntityId()).thenReturn("testStudent");
        when(testStudent.getBody()).thenReturn(body);
        when(testStudent.getDenormalizedData()).thenReturn(denormalized);
        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3")),
                underTest.findGoverningLEA(testStudent));
    }
}
