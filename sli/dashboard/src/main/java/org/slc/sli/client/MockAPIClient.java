package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.util.Constants;

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
    public GenericEntity[] getStudents(final String token, List<String> studentIds) {
        
        // Get all the students for that user (ignores sections)
        GenericEntity[] students = fromFileGeneric(getFilename("mock_data/" + token + "/student.json"), Constants.ENTITY_TYPE_STUDENT);
        
        // Filter out students that are not in our student list
        Vector<GenericEntity> filtered = new Vector<GenericEntity>();
        if (studentIds != null) {
            for (GenericEntity student : students) { 
                if (studentIds.contains(student.get("id"))) { 
                    filtered.add(student);
                }
            }
        }
        GenericEntity[] retVal = new GenericEntity[filtered.size()];
        return filtered.toArray(retVal);
    }
    
    @Override
    public GenericEntity[] getSchools(final String token) {
        return fromFileGeneric(getFilename("mock_data/" + token + "/school.json"), null);
    }
    
    @Override
    public GenericEntity[] getAssessments(final String token, List<String> studentIds) {

        GenericEntity[] assessments = fromFile(getFilename("mock_data/" + token + "/assessment.json"), null);

        Vector<GenericEntity> filtered = new Vector<GenericEntity>();
        // perform the filtering. 
        
        for (GenericEntity assessment : assessments) { 
            if (studentIds.contains(assessment.get("studentId"))) { 
                filtered.add(assessment);
            }
        }
        GenericEntity[] retVal = new GenericEntity[filtered.size()]; 
        return filtered.toArray(retVal);
    }

    @Override
    public GenericEntity[] getCustomData(String token, String key) {
        return fromFileGeneric(getFilename("mock_data/" + token + "/custom_" + key + ".json"), null);
    }
    
    
    @Override
    public AssessmentMetaData[] getAssessmentMetaData(final String token) {
        return fromFile(getFilename("mock_data/assessment_meta_data.json"), AssessmentMetaData[].class);
    }
    
    @Override
    public GenericEntity[] getStudentProgramAssociation(final String token, List<String> studentIds) {
        // Get programs list for ALL the student of that user (regardless of sections)
        GenericEntity[] programs = fromFileGeneric(getFilename("mock_data/" + token + "/student_program_association.json"), null);
        // perform the filtering. 
        Vector<GenericEntity> filtered = new Vector<GenericEntity>();
        if (studentIds != null) {
            // Collect programs for each and every student
            for (GenericEntity program : programs) { 
                if (studentIds.contains(program.get("studentId"))) { 
                    filtered.add(program);
                }
            }
        }
        GenericEntity[] retVal = new GenericEntity[filtered.size()];
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

    // Helper function to translate a .json file into generic entity object. 
    public static GenericEntity[] fromFileGeneric(String fileName, String type) {
    
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
            
            List<Map<String, Object>> maps = gson.fromJson(total, new TypeToken<List<Map<String, Object>>>() { } .getType());
            
            // populate list of generic entities
            GenericEntity[] entities = new GenericEntity[maps.size()];
            int i = 0;
            for (Map<String, Object> map : maps) {
                GenericEntity e = new GenericEntity();
                //e.setType(type);
                //e.setEntityId((String) (map.get("id")));
                //e.setBody(map);
                entities[i++] = e;
            }
            return entities;
            
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
