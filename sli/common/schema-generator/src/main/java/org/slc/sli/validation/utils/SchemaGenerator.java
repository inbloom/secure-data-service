package org.slc.sli.validation.utils;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData.AllowNull;

/**
 * Generate Avro schema from Ed-Fi XSD via JAXB.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class SchemaGenerator {
    
    /**
     * @param args
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {
        String clazz = "org.slc.sli.domain.TeacherSectionAssociation";
        
        Class<?> type = Class.forName(clazz);
        
        Schema schema = AllowNull.get().getSchema(type);
        
        System.out.println(schema.toString(true));
    }
    
}
