package org.slc.sli.util;

/**
 * This class is for constants that are used in multiple places throughout the application.
 * Constants used only in one class should be kept in that class.
 * 
 * @author dwu
 *
 */
public class Constants {
    // API related URLs
    public static final String API_SERVER_URI = "https://devapp1.slidev.org/api/rest";

    public static final String SESSION_CHECK_URL = "system/session/check";
    public static final String GET_ROLES_URL = "admin/roles";
    
    public static final String PROGRAM_ELL = "limitedEnglishProficiency";
    public static final String PROGRAM_FRE = "schoolFoodServicesEligibility";
    
    // view config strings - TODO: should these be changed to enums?
    public static final String VIEW_TYPE_STUDENT_LIST = "listOfStudents";
    public static final String FIELD_TYPE_ASSESSMENT = "assessment";
    public static final String FIELD_TYPE_STUDENT_INFO = "studentInfo";
    public static final String FIELD_LOZENGES_POSITION_FRONT = "pre";
    public static final String FIELD_LOZENGES_POSITION_BACK = "post";
    
    // model map keys
    public static final String MM_KEY_LOZENGE_CONFIG = "lozengeConfigs"; 
    public static final String MM_KEY_VIEW_CONFIG = "viewConfig"; 
    public static final String MM_KEY_ASSESSMENTS = "assessments"; 
    public static final String MM_KEY_STUDENTS = "students"; 
    public static final String MM_KEY_WIDGET_FACTORY = "widgetFactory";
    public static final String MM_KEY_CONSTANTS = "constants";
    
    //Program Participation Constants
    public static final String SHOW_ELL_LOZENGE = "Yes";
    
    // entity types
    public static final String ENTITY_TYPE_STUDENT = "student";
    
    /**
     * Contains the possible values for FRE participation
     */
    public static enum FREParticipation {
        FREE("Free"),
        REDUCED_PRICE("Reduced Price");
        
        private final String value;
        
        FREParticipation(String value) {
            this.value = value;
        }

        
        public String getValue() {
            return value;
        }
        
    }
    
    
}
