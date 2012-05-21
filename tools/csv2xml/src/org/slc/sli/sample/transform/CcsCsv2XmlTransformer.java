package org.slc.sli.sample.transform;

import java.util.HashMap;

import org.slc.sli.sample.entities.GradeLevelType;

import java.io.File;

import java.io.PrintStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slc.sli.sample.entities.ComplexObjectType;
import org.slc.sli.sample.entities.InterchangeAssessmentMetadata;

import java.io.IOException;
import java.util.Map;
import org.slc.sli.sample.entities.AcademicSubjectType;
import org.slc.sli.sample.entities.ContentStandardType;
import org.slc.sli.sample.entities.LearningStandard;
import org.slc.sli.sample.entities.LearningStandardId;

public class CcsCsv2XmlTransformer {
    private CcsCsvReader ccsCsvReader;
    private DotNotationToId dotNotationToId;
    private String outputLocation;
    private AcademicSubjectType academicSubjectType;
    private boolean ignoreNonExistentGuid;
    
    CcsCsv2XmlTransformer() {}
    
    void setCcsCsvReader(CcsCsvReader ccsCsvReader) {
        this.ccsCsvReader = ccsCsvReader;
    }
    
    void setDotNotationToId(DotNotationToId dotNotationToId) {
        this.dotNotationToId = dotNotationToId;
    }
    
    void setOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
    }
    
    void setAcademicSubjectType(AcademicSubjectType academicSubjectType) {
        this.academicSubjectType = academicSubjectType;
    }
    
    void setIgnoreNonExistentGuid(boolean ignoreNonExistentGuid) {
        this.ignoreNonExistentGuid = ignoreNonExistentGuid;
    }
    
    static abstract class DotNotationToId {
        abstract String getId(String dotNotation);
    }
    
    String getCopyright() {
        if(ccsCsvReader != null) {
            return ccsCsvReader.getCopyright();
        }
        return null;
    }
    
    /**
     * Iterate through common core standard csv records in the CSV files,
     * converts them into JAXB java objects, and then marshals them into SLI-EdFi xml file.
     * 
     * @throws JAXBException
     */
    void printLearningStandards() throws JAXBException, IOException {
        InterchangeAssessmentMetadata interchangeAssessmentMetadata = new InterchangeAssessmentMetadata();
        List<ComplexObjectType> learningStandards = interchangeAssessmentMetadata.getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();
        int learningStandardCounter = 0;
        while(ccsCsvReader.getCurrentRecord() != null) {
            LearningStandard learningStandard = this.getLearningStandard();
            if(learningStandard != null) {
                learningStandards.add(learningStandard);
                ccsCsvReader.getNextRecord();
                learningStandardCounter++;
            }
            else {
                ccsCsvReader.getNextRecord();
                continue;
            }
            
        }
        JAXBContext context = JAXBContext.newInstance(LearningStandard.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(interchangeAssessmentMetadata, new PrintStream(new File(outputLocation)));
        System.out.println("Total " + learningStandardCounter + " LearningStandards are exported.");
    }
    
    private LearningStandard getLearningStandard() throws IOException {
        Map<String, String> learningStandardRecord = ccsCsvReader.getCurrentRecord();
        String dotNotation = learningStandardRecord.get("ID");
        
        String id = dotNotationToId.getId(dotNotation);
        String guid = IdToGuidMapper.getInstance().getGuid(id);
        if(guid == null) {
            System.out.println("Dot Notation = <" + dotNotation + ">" + " ID = <" + id + "> does not have guid");
            if(ignoreNonExistentGuid) {
                return null;
            }
        }
        
        LearningStandardId learningStandardId = new LearningStandardId();
        learningStandardId.setIdentificationCode(guid);
        LearningStandard learningStandard = new LearningStandard();
        learningStandard.setLearningStandardId(learningStandardId);
        learningStandard.setContentStandard(ContentStandardType.STATE_STANDARD);
        String description = learningStandardRecord.get("State Standard");
        if (description == null || description.equals("")) {
            System.out.println("no description for" + id);
        }
        learningStandard.setDescription(description);
        learningStandard.setGradeLevel(getGradeLevel(dotNotation));
        learningStandard.setSubjectArea(academicSubjectType);
        return learningStandard;
    }
    
    private GradeLevelType getGradeLevel(String dotNotation) {
        String[] gradeLevels = dotNotation.split("\\.");
        GradeLevelType gradeLevel;
        int intGradeLevel = 0;
        try {
            intGradeLevel = Integer.parseInt(gradeLevels[0]);
        } catch (NumberFormatException e) {
            if (gradeLevels[0].toLowerCase().equals("k")) {
                intGradeLevel = 0;
            } else {
                
                // return Ninth grade for high school for now
                // TODO map the grade level for each high school math
                intGradeLevel = 9;
            }
        }
        
        switch (intGradeLevel) {
        case 0:
            gradeLevel = GradeLevelType.KINDERGARTEN;
            break;
        case 1:
            gradeLevel = GradeLevelType.FIRST_GRADE;
            break;
        case 2:
            gradeLevel = GradeLevelType.SECOND_GRADE;
            break;
        case 3:
            gradeLevel = GradeLevelType.THIRD_GRADE;
            break;
        case 4:
            gradeLevel = GradeLevelType.FOURTH_GRADE;
            break;
        case 5:
            gradeLevel = GradeLevelType.FIFTH_GRADE;
            break;
        case 6:
            gradeLevel = GradeLevelType.SIXTH_GRADE;
            break;
        case 7:
            gradeLevel = GradeLevelType.SEVENTH_GRADE;
            break;
        case 8:
            gradeLevel = GradeLevelType.EIGHTH_GRADE;
            break;
        case 9:
            gradeLevel = GradeLevelType.NINTH_GRADE;
            break;
        case 10:
            gradeLevel = GradeLevelType.TENTH_GRADE;
            break;
        case 11:
            gradeLevel = GradeLevelType.ELEVENTH_GRADE;
            break;
        case 12:
            gradeLevel = GradeLevelType.TWELFTH_GRADE;
            break;
        default:
            throw new RuntimeException("Should never reach here");
        }
        
        return gradeLevel;
    }
    
    private static class IdToGuidMapper {
        private final String identifiersCSVFile = "data/E0330_ccss_identifiers.csv";
        private Map<String, String> idToGuid = null;
        private static IdToGuidMapper instance = new IdToGuidMapper();
        
        private IdToGuidMapper() {}
        
        static synchronized IdToGuidMapper getInstance() throws IOException {
            if(instance == null) {
                instance = new IdToGuidMapper(); 
            }
            return instance;
        }
        
        String getGuid(String id) throws IOException {
            if(idToGuid == null) {
                idToGuid = new HashMap<String, String>();
                CcsCsvReader reader = new CcsCsvReader();
                reader.setFileLocation(identifiersCSVFile);
                reader.load();
                
                while (reader.getCurrentRecord() != null) {
                    Map<String, String> currentRecord = reader.getCurrentRecord();
                    idToGuid.put(currentRecord.get("Dot notation"),
                            currentRecord.get("GUID"));
                    reader.getNextRecord();
                }
            }
            return idToGuid.get(id);
        }
    }
}
