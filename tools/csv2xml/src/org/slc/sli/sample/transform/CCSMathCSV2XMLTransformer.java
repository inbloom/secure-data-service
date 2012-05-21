package org.slc.sli.sample.transform;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import java.util.Arrays;
import java.util.List;
import org.slc.sli.sample.entities.AcademicSubjectType;

/**
 * @author dliu
 * 
 *         Transform the Common Core Standard Math csv to edfi xml
 */
public class CCSMathCSV2XMLTransformer {
    // input csv files
    private static final String ccsCSVFile = "data/CC_Standards_6.25.10-Math.csv";
    
    // output Ed-Fi xml file
    private static final String interchangeCCSFile = "data/InterchangeAssessmentMetadata.xml";
    private static final String outputPath = "data/";

    private static String GRADE_LEVELS = "K12345678";
    private static Pattern PATTERN = Pattern.compile("^([^.]+).([^.]+).(.+)");
    public static void main(String[] args) throws Exception {
        CcsCsvReader learningStandardReader = new CcsCsvReader();
        learningStandardReader.setFileLocation(ccsCSVFile);
        learningStandardReader.setContainsCopyright(true);
        learningStandardReader.load();
        
        CcsCsv2XmlTransformer transformer = new CcsCsv2XmlTransformer();
        transformer.setCcsCsvReader(learningStandardReader);
        transformer.setOutputLocation(interchangeCCSFile);
        transformer.setAcademicSubjectType(AcademicSubjectType.MATHEMATICS);
        transformer.setDotNotationToId(new CcsCsv2XmlTransformer.DotNotationToId() {
            @Override
            String getId(String dotNotation) {
                String id = dotNotation.replaceAll(" ", "");
                Matcher matcher = PATTERN.matcher(id);
                if(matcher.matches()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(matcher.group(1)).append(".").append(matcher.group(2)).append(".").append(matcher.group(3).replace(".", ""));
                    id = sb.toString();
                }
                // add Math. to id if the id start with k-8,otherwise add Math.HS to id
                if (GRADE_LEVELS.indexOf(id.charAt(0)) >= 0) {
                    id = "Math." + id;
                } else {
                    id = "Math.HS" + id.replaceFirst("[.]", "-");
                }
                return id;
            }
        });
        transformer.printLearningStandards();
        SchemaValidator.check(outputPath);
        System.out.println(transformer.getCopyright());
    }
}
