package org.slc.sli.manager;

import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.view.HistoricalDataResolver;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * Class to manage historical data for view config that needs data added dynamically
 * @author jstokes
 */
public class HistoricalViewManager {

    HistoricalDataResolver historicalDataResolver;

    /**
     * Constructor
     * @param historicalDataResolver A resolver for student historical data
     */
    public HistoricalViewManager(HistoricalDataResolver historicalDataResolver) {
        this.historicalDataResolver = historicalDataResolver;
    }

    /**
     * Adds historical columns to a ViewConfig object (Grade and Course) for each session,
     * as far back as requested.
     * @param view The view to add columns to
     * @return A view config with historical columns added
     */
    public ViewConfig addHistoricalData(ViewConfig view) {
        String subjectArea = historicalDataResolver.getSubjectArea();
        SortedSet<String> schoolYears = historicalDataResolver.getSchoolYears();

        //TODO: limit the number of columns we can add
        for (Iterator<String> it = schoolYears.iterator(); it.hasNext();) {
            String schoolYear = it.next();
            
            DisplaySet historicalColumn = createSubjectAreaAndSchoolYear(subjectArea, schoolYear);
            Field course = createCourse(schoolYear);
            Field grade = createGrade(schoolYear);

            historicalColumn.getField().add(course);
            historicalColumn.getField().add(grade);
            view.getDisplaySet().add(historicalColumn);
        }

        return view;
    }
    
    private DisplaySet createSubjectAreaAndSchoolYear(String subjectArea, String schoolYear) {
        DisplaySet hist = new DisplaySet();
        hist.setDisplayName(subjectArea + " - " + schoolYear);
        return hist;
    }
    
    private Field createCourse(String schoolYear) {
        Field course = new Field();
        course.setDisplayName("Course");
        course.setType("historicalCourse");
        course.setValue("Course.title");
        course.setTimeSlot(schoolYear);
        return course;
    }
    
    private Field createGrade(String schoolYear) {
        Field grade = new Field();
        grade.setDisplayName("Grade");
        grade.setType("historicalGrade");
        grade.setValue("Course.grade");
        grade.setTimeSlot(schoolYear);
        return grade;
    }

}
