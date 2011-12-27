package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.Assessment;

import java.util.List;
import java.util.Vector;

/**
 * 
 * A mock API client
 * Each public method should correspond to a view in the "real" API.
 * Each public method's signature should have the following pattern:
 *  Entity[] getEntitys(final String token, List<String> filter1, List<String> filter2, ... ),   
 *  filter1, filter2, etc... correspond to the parameters passed into the API view. 
 *
 */
public class MockAPIClient implements APIClient {

    private ClassLoader classLoader;
    
    public MockAPIClient(){
        this.classLoader = Thread.currentThread().getContextClassLoader();
        
    }
    
    public Student[] getStudents(final String token) {
        URL url = classLoader.getResource("mockData/"+ token+"_mock_data.json");
        String filename = url.getFile();
        return fromFile(filename, Student[].class);
    }

    public School[] getSchools(final String token) {
        URL url = classLoader.getResource("mockData/"+ token.replaceAll("\\W", "") +"_mock_data.json");
        String filename = url.getFile();
        return fromFile(filename, School[].class);
    }
    public Assessment[] getAssessments(final String token, 
                                       List<String> studentIds) {

        URL url = classLoader.getResource("mockData/assessment_mock_data.json");
        String filename = url.getFile();        
        Assessment[] assessments = fromFile(filename, Assessment[].class);
        Vector<Assessment> filtered = new Vector<Assessment>();
        // perform the filtering. 
        for (Assessment assessment : assessments) { 
            if (studentIds.contains(assessment.getStudentId())) { 
                filtered.add(assessment);
            }
        }
        return (Assessment[]) filtered.toArray();
    }

    // Helper function to translate a .json file into object. 
    private static <T> T[] fromFile(String fileName, Class<T[]> c) {
        try {
            FileReader filein;
            filein = new FileReader(fileName);
            BufferedReader bin = new BufferedReader(filein);
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
        }
    }
    
    
}
