package org.slc.sli.api.security.pdp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests to verify functionality of v1 search mutator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
@TestExecutionListeners({WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
public class RootSearchMutatorTest {

    @Autowired
    RootSearchMutator rootSearchMutator;

    @Test
    public void testMutatePath() throws Exception {

        String expectedPath;
        String resultPath;

        // test valid student attendance search
        resultPath = rootSearchMutator.mutatePath(ResourceNames.ATTENDANCES, "studentId=some_id");
        expectedPath = "/" + ResourceNames.STUDENTS + "/" + "some_id" + "/" + ResourceNames.ATTENDANCES;
        Assert.assertEquals("Invalid mutation:", expectedPath, resultPath);

        resultPath = rootSearchMutator.mutatePath(ResourceNames.ATTENDANCES, "studentId=some_id&something=else");
        expectedPath = "/" + ResourceNames.STUDENTS + "/" + "some_id" + "/" + ResourceNames.ATTENDANCES;
        Assert.assertEquals("Invalid mutation:", expectedPath, resultPath);

        resultPath = rootSearchMutator.mutatePath(ResourceNames.ATTENDANCES, "key1=val1&studentId=some_id&key2=val2");
        expectedPath = "/" + ResourceNames.STUDENTS + "/" + "some_id" + "/" + ResourceNames.ATTENDANCES;
        Assert.assertEquals("Invalid mutation:", expectedPath, resultPath);

        // test valid student reportCard search, with additional reference fields, and multiple student ids
        resultPath = rootSearchMutator.mutatePath(ResourceNames.REPORT_CARDS, "schoolId=school_id&studentId=id1,id2,id3&key2=val2");
        expectedPath = "/" + ResourceNames.STUDENTS + "/" + "id1,id2,id3" + "/" + ResourceNames.REPORT_CARDS;
        Assert.assertEquals("Invalid mutation:", expectedPath, resultPath);

        // test invalid path mutation will return null because endpoint doesn't exist
        resultPath = rootSearchMutator.mutatePath(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "schoolId=school_id");
        expectedPath = null;
        Assert.assertEquals("Invalid mutation:", expectedPath, resultPath);

    }

}
