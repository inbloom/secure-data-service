package org.slc.sli.api.resources.v1;

/**
 * Constants used in URI requests.
 * 
 * 
 * @author kmyers
 *
 */
public class ParameterConstants {

    /**
     * An indication not to start from the first result.
     */
    public static final String OFFSET = "offset";
    
    /**
     * Maximum number of results to display at one time.
     */
    public static final String LIMIT = "limit";

    /**
     * An indication not to start from the first result.
     */
    public static final String DEFAULT_OFFSET = "0";
    
    /**
     * Maximum number of results to display at one time.
     */
    public static final String DEFAULT_LIMIT = "50";
    
    /**
     * Number of links to traverse when presenting a high-level document.
     */
    public static final String EXPAND_DEPTH = "expandDepth";

    /**
     * Entity reference to an ID in the school collection.
     */
    public static final String SCHOOL_ID = "schoolId";

    /**
     * Entity reference to an ID in the school collection.
     */
    public static final String TEACHER_SCHOOL_ASSOCIATION_ID = "teacherSchoolAssociationId";
    
    /**
     * Entity reference to an ID in the student collection.
     */
    public static final String STUDENT_ID = "studentId";
    public static final String ASSESSMENT_ID = "assessmentId";
    public static final String BELL_SCHEDULE_ID = "bellScheduleId";
    public static final String SECTION_ID = "sectionId";
    
    /**
     * Query parameter for fields to include.
     */
    public static final String INCLUDE_FIELDS = "includeFields";
    
    /**
     * Query parameter for fields to exclude.
     */
    public static final String EXCLUDE_FIELDS = "excludeFields";

}
