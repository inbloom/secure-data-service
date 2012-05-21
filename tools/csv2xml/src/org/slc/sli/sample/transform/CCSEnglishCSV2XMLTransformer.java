package org.slc.sli.sample.transform;

import org.slc.sli.sample.entities.AcademicSubjectType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCSEnglishCSV2XMLTransformer extends CSV2XMLTransformer {

    private static final String englishLearningStandardFile = "data/CC_Standards_6.25.10-English.csv";

    private static final String outputPath = "data/";

    private static Pattern PATTERN = Pattern.compile("^([^.]+).([^.]+).(.+)");
    public static void main(String args[]) throws Exception {
        CcsCsvReader englishLearningStandardReader = new CcsCsvReader();
        englishLearningStandardReader.setFileLocation(englishLearningStandardFile);
        englishLearningStandardReader.setContainsCopyright(true);
        englishLearningStandardReader.load();
        
        CcsCsv2XmlTransformer transformer = new CcsCsv2XmlTransformer();
        transformer.setCcsCsvReader(englishLearningStandardReader);
        transformer.setOutputLocation("data/Interchange-AssessmentMetadata.xml");
        transformer.setAcademicSubjectType(AcademicSubjectType.ENGLISH);
        transformer.setDotNotationToId(new CcsCsv2XmlTransformer.DotNotationToId() {
            private String convert(String prefix, String s) {
                Matcher matcher = PATTERN.matcher(s.trim());
                if(matcher.matches()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix).append(".").append(matcher.group(2)).append(".").append(matcher.group(1)).append(".").append(matcher.group(3).replace(".", ""));
                    return sb.toString();
                }
                return null;
            }
            @Override
            String getId(String dotNotation) {
                return convert("Literacy", dotNotation);
            }
        });
        // TODO: remove ignore when GUID is added to CSV
        transformer.setIgnoreNonExistentGuid(true);
        transformer.printLearningStandards();
        SchemaValidator.check(outputPath);
        System.out.println(transformer.getCopyright());
    }
}
