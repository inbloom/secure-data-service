package org.slc.sli.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;

public class MockAPIClient implements APIClient {

    public Student[] getStudents(final String token) throws IOException {

        FileReader filein = new FileReader("src/test/resources/student_mock_data.json");
        BufferedReader bin = new BufferedReader(filein);
        String s, total;
        total = "";
        while ((s = bin.readLine()) != null) {
            total += s;
        }
        Gson gson = new Gson();
        Student[] temp =  gson.fromJson(total, Student[].class);

        return temp;
    }
    
    
    public School[] getSchools(final String token) {
        FileReader filein;
        String filename = "src/test/resources/" + token + "_mock_data.json";
        try {
        
        filein = new FileReader(filename);
        
        BufferedReader bin = new BufferedReader(filein);
        String s, total;
        total = "";
        while ((s = bin.readLine()) != null) {
            total += s;
        }
        Gson gson = new Gson();        
        
        School[] temp = gson.fromJson(total, School[].class);
        return temp;
        } catch (IOException e) {
            
            System.err.println(e);
            return null;
        }
    }

}
