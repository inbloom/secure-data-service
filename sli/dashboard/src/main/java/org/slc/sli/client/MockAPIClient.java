package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.StudentProgramAssociation;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;

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
    	// Get all the students for that user (ignores sections)
        Student[] students = fromFile(getFilename("mock_data/" + token + "/student.json"), Student[].class);

        // Filter out students that are not in our student list
        Vector<Student> filtered = new Vector<Student>();
        if (studentIds != null) {
            for (Student student : students) { 
                if (studentIds.contains(student.getId())) { 
                    filtered.add(student);
                }
            }
        }
        Student[] retVal = new Student[filtered.size()];
        return filtered.toArray(retVal);
    }
    
    @Override
    public School[] getSchools(final String token) {
        return fromFile(getFilename("mock_data/" + token + "/school.json"), School[].class);
    }
    
    @Override
    public Assessment[] getAssessments(final String token, List<String> studentIds) {

        Assessment[] assessments = fromFile(getFilename("mock_data/" + token + "/assessment.json"), Assessment[].class);

        Vector<Assessment> filtered = new Vector<Assessment>();
        // perform the filtering. 
        
        for (Assessment assessment : assessments) { 
            if (studentIds.contains(assessment.getStudentId())) { 
                filtered.add(assessment);
            }
        }
        Assessment[] retVal = new Assessment[filtered.size()]; 
        return filtered.toArray(retVal);
    }

    @Override
    public CustomData[] getCustomData(String token, String key) {
        return fromFile(getFilename("mock_data/" + token + "/custom_" + key + ".json"), CustomData[].class);
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
    
    @Override
    public StudentProgramAssociation[] getStudentProgramAssociation(final String token, List<String> studentIds) {
		// Get programs list for ALL the student of that user (regardless of sections)
        StudentProgramAssociation[] programs = fromFile(getFilename("mock_data/" + token + "/student_program_association.json"), StudentProgramAssociation[].class);
        // perform the filtering. 
        Vector<StudentProgramAssociation> filtered = new Vector<StudentProgramAssociation>();
        if (studentIds != null) {
        	// Collect programs for each and every student
            for (StudentProgramAssociation program : programs) { 
                if (studentIds.contains(program.getStudentId())) { 
                    filtered.add(program);
                }
            }
        }
        StudentProgramAssociation[] retVal = new StudentProgramAssociation[filtered.size()];
        return filtered.toArray(retVal);
    }

    // Helper function to translate a .json file into object. 
    public static <T> T[] fromFile(String fileName, Class<T[]> c) {
    
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


}
