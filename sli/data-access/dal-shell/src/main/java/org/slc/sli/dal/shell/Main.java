package org.slc.sli.dal.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;

/**
 * Hello world!
 *
 */
@SuppressWarnings("unchecked")
public class Main 
{
    private final static BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    private final static ObjectMapper mapper = new ObjectMapper(); 
    private final static Repository<Entity> repo;
    
    static {
        String contextXML = ""; 
        ApplicationContext ctx = new GenericXmlApplicationContext( new ByteArrayResource( contextXML.getBytes() ) );
        repo = (Repository<Entity>) ctx.getBean("mongoEntityRepository"); 
    }
    
    public static void main(String[] args) {
        // args:
        // op inputfile outputile
        // 
        // input is a hash map from JSON 

        try {
            while(true) { 
                Map<String, Object> data = readInputItem(); 
                Map<String, Object> result = doCRUD(data); 
                writeOutputItem(result); 
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1); 
        }
    }
    

    private static Map<String, Object> readInputItem() throws IOException {
        String jsonStr = "";        
        String line = cin.readLine();
        while(!"---".equals(line)) {
            jsonStr += line + "\n"; 
            line = cin.readLine(); 
        }
        Map<String, Object> result = (HashMap<String,Object>)mapper.readValue(jsonStr, HashMap.class);
        return result; 
    }
    
    private static void writeOutputItem(Map<String, Object> outputItem) throws IOException {
        String jsonString = mapper.writeValueAsString(outputItem);
        System.out.println(jsonString); 
        System.out.println("---"); 
    }

    private static Map<String, Object> doCRUD(Map<String, Object> data) {
        
        
        // output is a hashmap from json 

        // read and write from the dal 

        // CRUD for studentSectionAssociation 
        //      includes CRUD for 
        return null;
    }
}
