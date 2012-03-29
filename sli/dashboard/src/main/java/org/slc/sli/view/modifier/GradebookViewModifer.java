package org.slc.sli.view.modifier;

import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.GradebookEntryResolver;

import java.util.SortedSet;

/**
 * Class to manage gradebook entry data for view config that needs data added dynamically
 * @author jstokes
 */
public class GradebookViewModifer implements ViewModifier {

    private SortedSet<GenericEntity> gradebookIds;

    protected static final String CURRENT = "Current";
    protected static final String GRADES = "<center>Unit Tests</center>";
    protected static final String DATE_FULFILLED = "dateFulfilled";
    protected static final String CURRENT_TERM = "currentTermGrade";
    protected static final String UNIT_TEST_GRADE = "unitTestGrade";
    protected static final String GRADEBOOK_ENTRY_TYPE = "gradebookEntryType";
    protected static final String GRADEBOOK_ENTRY_ID = "gradebookEntryId";

    /**
     * Constructor
     */
    public GradebookViewModifer(GradebookEntryResolver gradebookEntryResolver) {
        this.gradebookIds = gradebookEntryResolver.getGradebookIds();
    }

    /**
     * Delegates to the addGradebookEntries method to add the necessary columns to the specified view
     * @param view The view to modify
     * @return A modified view
     */
    @Override
    public ViewConfig modify(ViewConfig view) {
        return addGradebookEntries(view);
    }

    /**
     * Adds columns for gradebook entries
     * @param view The view to manipulate
     * @return view with added columns
     */
    private ViewConfig addGradebookEntries(ViewConfig view) {

        DisplaySet unitTests = createUnitTests();
        if (unitTests != null) {
            view.getDisplaySet().add(unitTests);
        }

        return view;
    }

    private DisplaySet createUnitTests() {
        if (gradebookIds.isEmpty()) { return null; }

        DisplaySet unitTests = new DisplaySet();
        unitTests.setDisplayName("Current");

        for (GenericEntity entry : gradebookIds) {
            Field unitField = createUnitTest(entry.getString(GRADEBOOK_ENTRY_TYPE), entry.getString(GRADEBOOK_ENTRY_ID));
            if (unitField != null) {
                unitTests.getField().add(unitField);
            }
        }

        return unitTests;
    }

    private Field createUnitTest(String testType, String testName) {
        if (testType == null || testName == null) { return null; }

        Field unitTest = new Field();

        unitTest.setDisplayName(testType);
        unitTest.setType(UNIT_TEST_GRADE);
        unitTest.setTimeSlot(testName);
        unitTest.setValue(testName);

        return unitTest;
    }
}
