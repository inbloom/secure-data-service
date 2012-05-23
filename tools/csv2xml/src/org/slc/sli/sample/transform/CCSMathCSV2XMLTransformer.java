package org.slc.sli.sample.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.sample.entities.AcademicSubjectType;
import org.slc.sli.sample.entities.LearningObjective;
import org.slc.sli.sample.entities.LearningStandardId;
import org.slc.sli.sample.entities.LearningStandardIdentityType;
import org.slc.sli.sample.entities.LearningStandardReferenceType;
import org.slc.sli.sample.transform.CcsCsv2XmlTransformer.LearningStandardResult;

/**
 * @author dliu
 *
 *         Transform the Common Core Standard Math csv to edfi xml
 */
public class CCSMathCSV2XMLTransformer {
    // input csv files
    private static final String ccsCSVFile = "data/CC_Standards_6.25.10-Math.csv";

    // output Ed-Fi xml file
    private static final String interchangeCCSFile = "data/InterchangeAssessmentMetadata-CCS-Math.xml";
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

        // TODO set the learningObjectiveGenerator to handle difference between math and english to
        // build common core standard hierarchy, the code is the same as in english csv to xml
        // transformer and no learningObjective hierarchy is created for now
        transformer.setLearningObjectiveGenerator(new CcsCsv2XmlTransformer.LearningObjectiveGenerator() {
            @Override
            Collection<LearningObjective> generateLearningObjectives(
                    Map<String, Collection<CcsCsv2XmlTransformer.LearningStandardResult>> learningObjectiveIdToLearningStandardResults,
                    Map<String, String> idToGuidMap)
                    throws IOException {
                Collection<LearningObjective> learningObjectives = new ArrayList<LearningObjective>();
                for (String key : learningObjectiveIdToLearningStandardResults.keySet()) {
                    Collection<LearningStandardResult> learningStandardResults = learningObjectiveIdToLearningStandardResults
                            .get(key);
                    LearningObjective learningObjective = new LearningObjective();
                    LearningStandardId learningStandardId = new LearningStandardId();
                    learningStandardId.setIdentificationCode(CcsCsv2XmlTransformer.IdToGuidMapper.getInstance()
                            .getGuid(key));
                    learningObjective.setLearningObjectiveId(learningStandardId);

                    LearningStandardResult firstLearningStandardResult = learningStandardResults.iterator().next();
                    for (LearningStandardResult learningStandardResult : learningStandardResults) {
                        LearningStandardReferenceType learningStandardReferenceType = new LearningStandardReferenceType();
                        LearningStandardIdentityType learningStandardIdentityType = new LearningStandardIdentityType();
                        learningStandardIdentityType.setLearningStandardId(learningStandardResult.getLearningStandard()
                                .getLearningStandardId());
                        learningStandardReferenceType.setLearningStandardIdentity(learningStandardIdentityType);
                        learningObjective.getLearningStandardReference().add(learningStandardReferenceType);
                    }
                    learningObjective.setObjective(firstLearningStandardResult.getCategory());
                    learningObjective.setAcademicSubject(firstLearningStandardResult.getLearningStandard()
                            .getSubjectArea());
                    learningObjective.setObjectiveGradeLevel(firstLearningStandardResult.getLearningStandard()
                            .getGradeLevel());
                    learningObjectives.add(learningObjective);
                }
                return learningObjectives;
            }
        });
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
