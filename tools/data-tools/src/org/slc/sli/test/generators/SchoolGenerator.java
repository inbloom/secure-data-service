package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.School;

public class SchoolGenerator {
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
    }
    
    public static School generate(String schoolId) {
        School school = new School();
        school.setId(schoolId);
        
        
        return school;
    }
}
