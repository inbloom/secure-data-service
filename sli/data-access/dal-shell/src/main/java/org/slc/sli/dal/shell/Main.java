package org.slc.sli.dal.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Hello world!
 *
 */
public class Main 
{
    private final static BufferedReader cin; 
    static {
        cin = new BufferedReader(new InputStreamReader(System.in));
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // args:
        // op inputfile outputile
        // 
        // input is a hash map from JSON 

        try {
            String jsonStr = readInputItem(); 
            ObjectMapper mapper = new ObjectMapper(); 
            Map<String, Object> data = (HashMap<String,Object>)mapper.readValue(jsonStr, HashMap.class);
            System.out.println("Result:\n------------------\n" + data + "\n-----------------");
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // output is a hashmap from json 

        // read and write from the dal 

        // CRUD for studentSectionAssociation 
        //      includes CRUD for 
    }
    
    private static String readInputItem() {
        String result = "";        
        try {
            String line = cin.readLine();
            while(!"---".equals(line)) {
                result += line + "\n"; 
                line = cin.readLine(); 
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result; 
    }
}
