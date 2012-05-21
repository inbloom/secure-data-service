package org.slc.sli.sample.transform;

import org.slc.sli.sample.entities.GradeLevelType;

import org.slc.sli.sample.entities.ContentStandardType;

import org.slc.sli.sample.entities.AcademicSubjectType;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.slc.sli.sample.entities.ComplexObjectType;
import org.slc.sli.sample.entities.InterchangeAssessmentMetadata;
import org.slc.sli.sample.entities.LearningStandard;
import org.slc.sli.sample.entities.LearningStandardId;

public class CCSEnglishCSV2XMLTransformer extends CSV2XMLTransformer {

    private static final String englishLearningStandardFile = "data/CC_Standards_6.25.10-English.csv";
    private static final String interchangeAssessmentMetadataFile = "data/Interchange-AssessmentMetadata.xml";
    private static final String outputPath = "data/";

    private CcsCsvReader englishLearningStandardReader;

    private void loadData() throws Exception {
        englishLearningStandardReader = new CcsCsvReader(englishLearningStandardFile);
    }
    
    private String getCopyright() {
        if(englishLearningStandardReader != null) {
            return englishLearningStandardReader.getCopyright();
        }
        return null;
    }

    private void printLearningStandards(PrintStream ps) throws JAXBException, IOException {
        InterchangeAssessmentMetadata interchangeAssessmentMetadata = new InterchangeAssessmentMetadata();
        List<ComplexObjectType> list = interchangeAssessmentMetadata.getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();
        int learningStandardCounter = 0;
        while(englishLearningStandardReader.getCurrentRecord() != null) {
            LearningStandard learningStandard = this.getLearningStandard();
            if(learningStandard != null) {
                list.add(this.getLearningStandard());
            }
            else {
                break;
            }
            englishLearningStandardReader.getNextRecord();
            learningStandardCounter++;
        }
        JAXBContext context = JAXBContext.newInstance(LearningStandard.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(interchangeAssessmentMetadata, ps);
        System.out.println("Total " + learningStandardCounter + " LearningStandards are exported.");
    }

    private LearningStandard getLearningStandard() {
        Map<String, String> learningStandardRecord = englishLearningStandardReader.getCurrentRecord();
        String dotNotation = learningStandardRecord.get("ID");
        if(dotNotation == null) {
            return null;
        }
        String id = convert("Literacy", dotNotation);
        if(id == null) {
            return null;
        }
        LearningStandard learningStandard = new LearningStandard();
        LearningStandardId learningStandardId = new LearningStandardId();
        learningStandardId.setIdentificationCode(id);
        learningStandard.setLearningStandardId(learningStandardId);
        learningStandard.setContentStandard(ContentStandardType.STATE_STANDARD);
        learningStandard.setDescription(learningStandardRecord.get("State Standard"));
        learningStandard.setGradeLevel(getLSGradeLevel(dotNotation));
        learningStandard.setSubjectArea(AcademicSubjectType.ENGLISH);
        return learningStandard;
    }

    private GradeLevelType getLSGradeLevel(String dotNotation) {
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
                gradeLevel = GradeLevelType.TWELFTH_GRADE;

        }
        return gradeLevel;

    }
    private static Pattern PATTERN = Pattern.compile("^([^.]+).([^.]+).(.+)");
    private String convert(String prefix, String s) {
        Matcher matcher = PATTERN.matcher(s.trim());
        if(matcher.matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(".").append(matcher.group(2)).append(".").append(matcher.group(1)).append(".").append(matcher.group(3));
            return sb.toString();
        }
        return null;
    }

    public static void main(String args[]) throws Exception {
        CCSEnglishCSV2XMLTransformer transformer = new CCSEnglishCSV2XMLTransformer();
        transformer.loadData();
        File file = new File(interchangeAssessmentMetadataFile);
        PrintStream ps = new PrintStream(file);
        transformer.printLearningStandards(ps);
        SchemaValidator.check(outputPath);
        System.out.println(transformer.getCopyright());
    }
}
