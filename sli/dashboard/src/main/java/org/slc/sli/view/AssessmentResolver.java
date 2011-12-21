package org.slc.sli.view;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Student;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

//Hopefully there will be one for each of dataSet types

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment data passed onto
 *  dashboard views. Contains useful tools look up assessment data
 * 
 * @author syau
 *
 */
public class AssessmentResolver {
    List<Assessment> assessments;
    
    /**
     * Constructor
     */
    public AssessmentResolver(List<Assessment> a) { assessments = a; }
    
    /**
     * Looks up a representation for the result of the assessment, taken by the student 
     * in the specified timeslot 
     * 
     * Returns the string representation of the result, or empty string if the result doesn't exist
     */
    public String get(String assName, Student student, String timeSlot, String field) {

        // This first implementation is gruelingly inefficient. But, whateves... it's gonna be 
        // thrown away. 

        // A) filter out students and assessment first
        List<Assessment> studentFiltered = new ArrayList();
        for (Assessment a : assessments) {
            if (a.getStudentId().equals(student.getUid()) && a.getAssessmentName().equals(assName)) {
                studentFiltered.add(a);
            }
        }
        if (studentFiltered.isEmpty()) { return ""; }
        // B) apply time logic. For now, just get the latest
        //    Sort the assessments in descending order of assessment year and get the first one
        Collections.sort(studentFiltered, new Comparator<Assessment>() {
            public int compare(Assessment o1, Assessment o2) {
                return o1.getYear() - o2.getYear();
            }
        });
        Assessment chosenAssessment = studentFiltered.get(0);
        if (field.equals("level")) { return chosenAssessment.getPerfLevelAsString(); }
        if (field.equals("scaleScore")) { return chosenAssessment.getScaleScoreAsString(); }
        if (field.equals("percentile")) { return chosenAssessment.getPercentileAsString(); }
        if (field.equals("lexileScore")) { return chosenAssessment.getLexileScoreAsString(); }
        return ""; 
    }
}
