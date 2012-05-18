package org.slc.sli.test.edfi.entities.meta.relations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;
import org.slc.sli.test.edfi.entities.meta.GradeBookEntryMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.ReportCardMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.generators.LearningStandardGenerator;

public class StudentGradeRelations {

    public static List<String>           learningObjectives     = new LinkedList<String>();
    public static List<String>  studentCompetencyObjectives     = new LinkedList<String>();
    public static List<String>   competencyLevelDescriptors     = new LinkedList<String>();
     
    public static final int COMPETENCY_LEVEL_DESCRIPTOR           = 2;
    
    //For every Student
    //    There is one SAR Per Session(SAR is the summary of the Student's performance for that Session) 
    //         Multiple CourseTranscripts point to the SAR
    
    //SAR has ReportCard for every Grading Period
    //    A ReportCard has Grades for every Section
    //    A ReportCard has StudentCompetencies
    //        A StudentCompetency has LearningObjective|StudentCompetencyObjective and CompetencyLevelDescriptor 
            
    public static final int REPORT_CARDS                              = 1;  
    
    public static final int LEARNING_OBJECTIVES_PER_REPORT            = 1;       
    public static final int STUDENT_COMPETENCY_OBJECTIVE_PER_REPORT   = 1;
    
    public static final int GRADEBOOK_ENTRIES                         = 1;
    public static final int LEARNING_OBJECTIVES_PER_GRADEBOOKENTRY    = 1;
    public static final int INV_PROBABILITY_STUDENT_HAS_GRADEBOOKENTRY = 43;
    
    public static final LearningStandard LEARNING_STANDARD            = LearningStandardGenerator.generateLowFi("LearningStandardCode");
    public static final LearningStandardReferenceType LEARNING_STANDARD_REF        
                                                                      = LearningStandardGenerator.getLearningStandardReferenceType("LearningStandardCode");  
    
    public static List<GradeBookEntryMeta> GRADE_BOOK_ENTRY_METAS     = new ArrayList<GradeBookEntryMeta>();
    public static List<ReportCardMeta> REPORT_CARD_META               = new ArrayList<ReportCardMeta>();
    
    public static final Random RAND                                   = new Random();
    private static List<SectionMeta> SECTIONS                         = null;
    private static final int GRADING_PERIOD_START_YEAR                = 1996;
    private static final int GRADING_PERIOD_MAX_START_YEAR            = 2030;
    private static final int GRADING_PERIOD_INTERVAL                  = GRADING_PERIOD_MAX_START_YEAR - GRADING_PERIOD_START_YEAR;
    
    public static void buildGradeBookEntriesMeta(){
        for(int i = 0; i < GRADEBOOK_ENTRIES; i++){
            GradeBookEntryMeta gbeMeta = new GradeBookEntryMeta();
            GRADE_BOOK_ENTRY_METAS.add(gbeMeta);
            
            
            GradingPeriodMeta gpMeta = new GradingPeriodMeta();
            int startYear = GRADING_PERIOD_START_YEAR + (i % GRADING_PERIOD_INTERVAL);
            gpMeta.setBeginData(String.valueOf(startYear));
            gpMeta.setEndDate(String.valueOf(startYear + 1));
            gbeMeta.setGradingPeriod(gpMeta);
            
            List<String> gradeBookEntryObjectives = new ArrayList<String>();
            for(int j = 0 ; j < LEARNING_OBJECTIVES_PER_GRADEBOOKENTRY; j++){
                String loId = "learningObjectiveId_g" + i + "_" + j;
                learningObjectives.add(loId);
                gradeBookEntryObjectives.add(loId);
            }                       
            gbeMeta.setLearningObjectiveIds(gradeBookEntryObjectives);
            gbeMeta.setId("GBE_" + i);
            gbeMeta.setSection(getRandomSection());
        }
    }
    
    public static void buildReportCardMeta(){
        for(int i = 0; i < REPORT_CARDS; i++){
            ReportCardMeta rcMeta = new ReportCardMeta();
            rcMeta.setId("RC_" + i);
            REPORT_CARD_META.add(rcMeta);
            
            
            GradingPeriodMeta gpMeta = new GradingPeriodMeta();
            int startYear = GRADING_PERIOD_START_YEAR + (i % GRADING_PERIOD_INTERVAL);
            gpMeta.setBeginData(String.valueOf(startYear));
            gpMeta.setEndDate(String.valueOf(startYear + 1));
            rcMeta.setGradingPeriod(gpMeta);
            
            List<String> reportLearningObjectives = new ArrayList<String>();
            List<SectionMeta> reportLearningObjectiveSections = new ArrayList<SectionMeta>();
            for(int j = 0 ; j < LEARNING_OBJECTIVES_PER_REPORT; j++){
                String loId = "learningObjectiveId_r" + i + "_" + j;
                learningObjectives.add(loId);
                reportLearningObjectives.add(loId);
                reportLearningObjectiveSections.add(getRandomSection());
            }
            rcMeta.setLearningObjectiveIds(reportLearningObjectives);
            rcMeta.setLearningObjectiveSections(reportLearningObjectiveSections);
            
            List<String> reportStudentCompetencyObjectives = new ArrayList<String>();
            List<SectionMeta> reportStudentCompetencyObjectiveSections = new ArrayList<SectionMeta>();
            for(int k = 0 ; k < STUDENT_COMPETENCY_OBJECTIVE_PER_REPORT; k++){
                String loId = "SCObjectiveId_r" + i + "_" + k;
                studentCompetencyObjectives.add(loId);
                reportStudentCompetencyObjectives.add(loId);
                reportStudentCompetencyObjectiveSections.add(getRandomSection());
            }
            rcMeta.setStudentCompetencyIds(reportStudentCompetencyObjectives);
            rcMeta.setStudentCompetencyObjectiveSections(reportStudentCompetencyObjectiveSections);
        }
    }
    
    public static void buildCompetencyLevelDescriptorMeta(){
        for(int i = 0; i < COMPETENCY_LEVEL_DESCRIPTOR; i++){
            competencyLevelDescriptors.add("CLD_" + i);
        }
    }
    
    private static SectionMeta getRandomSection(){
        if(SECTIONS == null) SECTIONS = new ArrayList<SectionMeta>(MetaRelations.SECTION_MAP.values());
        return SECTIONS.get(RAND.nextInt(SECTIONS.size()));
    }
}
