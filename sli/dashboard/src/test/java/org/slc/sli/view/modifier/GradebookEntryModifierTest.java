package org.slc.sli.view.modifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.unit.view.HistoricalDataResolverTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for GradeBookEntryViewManager
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class GradebookEntryModifierTest {
    private static Logger log = LoggerFactory.getLogger(HistoricalDataResolverTest.class);

    private GradebookViewModifer gradebookViewModifer; // class under test

    @Mock
    private Comparator<GenericEntity> comparator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(comparator.compare(any(GenericEntity.class), any(GenericEntity.class))).thenReturn(1);
        SortedSet<GenericEntity> gradebookIds = new TreeSet<GenericEntity>(comparator);

        GenericEntity ge1 = new GenericEntity();
        ge1.put(GradebookViewModifer.DATE_FULFILLED, "10-11-2011");

        GenericEntity ge2 = new GenericEntity();
        ge2.put(GradebookViewModifer.DATE_FULFILLED, "10-15-2011");

        gradebookIds.add(ge1);
        gradebookIds.add(ge2);

        gradebookViewModifer = new GradebookViewModifer(gradebookIds);
    }

    @Test
    public void testModify() {
        ViewConfig testView = new ViewConfig();
        testView = gradebookViewModifer.modify(testView);

        List<DisplaySet> testDisplaySet = testView.getDisplaySet();
        assertEquals(2, testDisplaySet.size());

        DisplaySet current = testDisplaySet.get(0);
        DisplaySet grades = testDisplaySet.get(1);
        assertEquals(GradebookViewModifer.CURRENT, current.getDisplayName());
        assertEquals(GradebookViewModifer.GRADES, grades.getDisplayName());

        assertEquals(1, current.getField().size());
        Field average = current.getField().get(0);
        assertEquals(GradebookViewModifer.AVERAGE, average.getDisplayName());

        assertEquals(2, grades.getField().size());
        Field firstUnitTest = grades.getField().get(0);
        Field secondUnitTest = grades.getField().get(1);

        assertEquals("10-11-2011", firstUnitTest.getDisplayName());
        assertEquals("10-15-2011", secondUnitTest.getDisplayName());
    }

}
