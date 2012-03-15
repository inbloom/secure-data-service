package org.slc.sli.unit.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.GradebookEntryViewManager;
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
public class GradebookEntryViewManagerTest {
    private static Logger log = LoggerFactory.getLogger(HistoricalDataResolverTest.class);

    private GradebookEntryViewManager gradebookEntryViewManager; // class under test

    private static final String CURRENT = "Current";
    private static final String GRADES = "<center>Unit Tests</center>";
    private static final String AVERAGE = "Average";

    @Mock
    private Comparator<GenericEntity> comparator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(comparator.compare(any(GenericEntity.class), any(GenericEntity.class))).thenReturn(1);
        SortedSet<GenericEntity> gradebookIds = new TreeSet<GenericEntity>(comparator);

        GenericEntity ge1 = new GenericEntity();
        ge1.put("id", "1234GBE");
        ge1.put("dateFulfilled", "10-11-2011");

        GenericEntity ge2 = new GenericEntity();
        ge2.put("id", "4567GBE");
        ge2.put("dateFulfilled", "10-15-2011");

        gradebookIds.add(ge1);
        gradebookIds.add(ge2);

        gradebookEntryViewManager = new GradebookEntryViewManager(gradebookIds);
    }

    @Test
    public void testAddGradebookEntries() {
        ViewConfig testView = new ViewConfig();
        testView = gradebookEntryViewManager.addGradebookEntries(testView);

        List<DisplaySet> testDisplaySet = testView.getDisplaySet();
        assertEquals(2, testDisplaySet.size());

        DisplaySet current = testDisplaySet.get(0);
        DisplaySet grades = testDisplaySet.get(1);
        assertEquals(CURRENT, current.getDisplayName());
        assertEquals(GRADES, grades.getDisplayName());

        assertEquals(1, current.getField().size());
        Field average = current.getField().get(0);
        assertEquals(AVERAGE, average.getDisplayName());

        assertEquals(2, grades.getField().size());
        Field firstUnitTest = grades.getField().get(0);
        Field secondUnitTest = grades.getField().get(1);

        assertEquals("10-11-2011", firstUnitTest.getDisplayName());
        assertEquals("10-15-2011", secondUnitTest.getDisplayName());
    }

}
