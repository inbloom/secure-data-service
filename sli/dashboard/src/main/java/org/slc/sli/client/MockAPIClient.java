package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slc.sli.entity.Course;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Section;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;

import java.util.List;
import java.util.Vector;

/**
 * 
 * A mock API client
 */
public class MockAPIClient implements APIClient {

    private ClassLoader classLoader;
    
    public MockAPIClient() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    

    @Override
    public Student[] getStudents(final String token, List<String> studentIds) {
        Student[] students = fromFile(getFilename("mock_data/" + token.replaceAll("\\W", "") + "/student.json"), Student[].class);
        // perform the filtering. 
        Vector<Student> filtered = new Vector<Student>();
        if (studentIds != null)
            for (Student student : students) { 
                if (studentIds.contains(student.getId())) { 
                    filtered.add(student);
                }
            }
        Student[] retVal = new Student[filtered.size()];
        return filtered.toArray(retVal);
    }
    @Override
    public School[] getSchools(final String token) {
        return fromFile(getFilename("mock_data/" + token.replaceAll("\\W", "") + "/school.json"), School[].class);
    }
    
    @Override
    public Assessment[] getAssessments(final String token, List<String> studentIds) {
        String filename = "src/test/resources/mock_data/" + token + "/assessment.json";
        Assessment[] assessments = fromFile(getFilename("mock_data/" + token.replaceAll("\\W", "") + "/assessment.json"), Assessment[].class);
        Vector<Assessment> filtered = new Vector<Assessment>();
        // perform the filtering. 
        /* TODO: Start filtering again, when we actually get student uids from a list
        for (Assessment assessment : assessments) { 
            if (studentIds.contains(assessment.getStudentId())) { 
                filtered.add(assessment);
            }
        }
        Assessment[] retVal = new Assessment[filtered.size()]; 
        return filtered.toArray(retVal);
        */
        return assessments;
    }

    @Override
    public CustomData[] getCustomData(String token, String key) {
        return fromFile(getFilename("mock_data/" + token.replaceAll("\\W", "") + "/custom_" + key + ".json"), CustomData[].class);
    }
    
    @Override
    public void saveCustomData(CustomData[] src, String token, String key) {
        String filename = "src/test/resources/mock_data/" + token + "/custom_" + key + ".json";
        toFile(src, filename, CustomData[].class);
    }
    
    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return fromFile(getFilename("mock_data/assessment_meta_data.json"), AssessmentMetaData[].class);
    }

    // Helper function to translate a .json file into object. 
    private static <T> T[] fromFile(String fileName, Class<T[]> c) {
    
        BufferedReader bin = null;
    
        try {
            FileReader filein;
            filein = new FileReader(fileName);
            bin = new BufferedReader(filein);
            String s, total;
            total = "";
            while ((s = bin.readLine()) != null) {
                total += s;
            }
            Gson gson = new Gson();        
            T[] temp = gson.fromJson(total, c);
            return temp;
            
        } catch (IOException e) {
            System.err.println(e);
            return null;
            
        } finally {

            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
    
    // Helper function to translate an object into a .json file 
    private static <T> void toFile(T[] src, String fileName, Class<T[]> c) {
        
        BufferedWriter bout = null;
        
        try {
            FileWriter fileOut = new FileWriter(fileName);
            bout = new BufferedWriter(fileOut);
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String strOut = gson.toJson(src, c);
            bout.write(strOut);
            
        } catch (IOException e) {
            System.err.println(e);
        } finally {

            try {
                if (bout != null) {
                    bout.flush();
                    bout.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
    
    public String getFilename(String filename) {
        URL url = classLoader.getResource(filename);
        return url.getFile();
    }


    @Override
    public String getTeacherId(String token) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Section[] getSectionsForTeacher(String id, String token) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Course[] getCoursesForSections(Section[] sections, String token) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public School[] getSchoolsForCourses(Course[] courses, String token) {
        // TODO Auto-generated method stub
        return null;
    }

}
