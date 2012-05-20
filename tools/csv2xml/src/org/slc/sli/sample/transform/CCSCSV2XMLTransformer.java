package org.slc.sli.sample.transform;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.slc.sli.sample.entities.ComplexObjectType;
import org.slc.sli.sample.entities.ContentStandardType;
import org.slc.sli.sample.entities.GradeLevelType;
import org.slc.sli.sample.entities.InterchangeAssessmentMetadata;
import org.slc.sli.sample.entities.LearningStandard;
import org.slc.sli.sample.entities.LearningStandardId;

public class CCSCSV2XMLTransformer {
    // read CCS csv file
    private CSVReader ccsReader;
    
    // input csv files
    private static final String ccsCSVFile = "data/CC_Standards_6.25.10.csv";

    // output Ed-Fi xml file
    private static final String interchangeCCSFile = "data/InterchangeAssessmentMetadata.xml";
    private static final String outputPath = "data/";
    
    /**
     * main method
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CCSCSV2XMLTransformer transformer = new CCSCSV2XMLTransformer();
        transformer.loadData();
        
        PrintStream ps = new PrintStream(new File(interchangeCCSFile));
        transformer.printInterchangeCCS(ps);
        
        SchemaValidator.check(outputPath);
    }
    
    /**
     * open csv files and create CSV reader for each file
     * and load the first record for each reader
     * 
     * @throws IOException
     */
    private void loadData() throws IOException {
        // load CCS data
        ccsReader = new CSVReader(ccsCSVFile);
        
    }
    
    /**
     * Iterate through Student, Parent, and studentParentAssociation records in the CSV files,
     * converts them into JAXB java objects, and then marshals them into SLI-EdFi xml file.
     * 
     * @param ps
     * @throws JAXBException
     */
    private void printInterchangeCCS(PrintStream ps) throws JAXBException {
        int learningStandardCounter = 0;
        
        Marshaller marshaller = getMarshaller();
        InterchangeAssessmentMetadata interchangeCCS = new InterchangeAssessmentMetadata();
        
        List<ComplexObjectType> learningStandards = interchangeCCS
                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();

        // process student
        while (ccsReader.getCurrentRecord() != null) {
            learningStandards.add(this.getLearningStandard());
            ccsReader.getNextRecord();
            learningStandardCounter++;
        }
        
        marshaller.marshal(interchangeCCS, ps);
        
        System.out.println("Total " + learningStandardCounter + " LearningStandards are exported.");
    }
    
    private Marshaller getMarshaller() throws JAXBException, PropertyException {
        JAXBContext context = JAXBContext.newInstance(LearningStandard.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        return marshaller;
    }
    
    private LearningStandard getLearningStandard() {
        
        Map<String, String> learningStandardRecord = ccsReader.getCurrentRecord();
        LearningStandard learningStandard = new LearningStandard();
        LearningStandardId learningStandardId = new LearningStandardId();
        String dotNotation = learningStandardRecord.get("ID");
        learningStandardId.setIdentificationCode(getLSIdentificationCode(dotNotation));
        learningStandard.setLearningStandardId(learningStandardId);
        learningStandard.setContentStandard(ContentStandardType.STATE_STANDARD);
        learningStandard.setDescription(learningStandardRecord.get("State Standard"));
        learningStandard.setGradeLevel(getLSGradeLevel(dotNotation));
        return learningStandard;
    }
    
    private String getLSIdentificationCode(String dotNotation) {
        // return dotNotation right now
        // TODO use mapping between learning standard dot notation and identification code to return
        // correct identification code
        return dotNotation;
    }
    
    private GradeLevelType getLSGradeLevel(String dotNotation) {
        String[] gradeLevels = dotNotation.split("//.");
        GradeLevelType gradeLevel;
        int intGradeLevel=0;
        try{
        intGradeLevel = Integer.parseInt(gradeLevels[0]);
        } catch (NumberFormatException e) {
            // return twelfth grade for now if first character of dotNotation is not number
            // TODO need to map grade level to first character of dotNotation which is not a number
            intGradeLevel = 12;
        }

        switch (intGradeLevel) {
            case 1:
                gradeLevel = GradeLevelType.FIRST_GRADE;
            case 2:
                gradeLevel = GradeLevelType.SECOND_GRADE;
            case 3:
                gradeLevel = GradeLevelType.THIRD_GRADE;
            case 4:
                gradeLevel = GradeLevelType.FOURTH_GRADE;
                
            case 5:
                gradeLevel = GradeLevelType.FIFTH_GRADE;
            case 6:
                gradeLevel = GradeLevelType.SIXTH_GRADE;
            case 7:
                gradeLevel = GradeLevelType.SEVENTH_GRADE;
            case 8:
                gradeLevel = GradeLevelType.EIGHTH_GRADE;
            case 9:
                gradeLevel = GradeLevelType.NINTH_GRADE;
            case 10:
                gradeLevel = GradeLevelType.TENTH_GRADE;
            case 11:
                gradeLevel = GradeLevelType.ELEVENTH_GRADE;
            case 12:
                gradeLevel = GradeLevelType.TWELFTH_GRADE;
            default:
                gradeLevel = GradeLevelType.TWELFTH_GRADE;

        }
        return gradeLevel;

    }

}
