/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.sample.transform;

import org.slc.sli.sample.transform.CcsCsv2XmlTransformer.GradeLevelMapper;

import org.slc.sli.sample.entitiesR1.GradeLevelType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.sample.entitiesR1.AcademicSubjectType;
import org.slc.sli.sample.entitiesR1.LearningObjective;
import org.slc.sli.sample.entitiesR1.LearningStandardId;
import org.slc.sli.sample.entitiesR1.LearningStandardIdentityType;
import org.slc.sli.sample.entitiesR1.LearningStandardReferenceType;
import org.slc.sli.sample.transform.CcsCsv2XmlTransformer.LearningStandardResult;

public class CCSEnglishCSV2XMLTransformer {

    private static final String englishLearningStandardFile = "data/CC_Standards_6.25.10-English.csv";

    private static final String outputPath = "data/";

    private static Pattern PATTERN = Pattern.compile("^([^.]+).([^.]+).(.+)");
    
    static class EnglishGradeLevelMapper extends GradeLevelMapper {
        @Override
        int getGradeLevel(String s) {
            String gradeLevel = s.split("\\.")[0];
            if(gradeLevel.indexOf('-') > 0) {
                gradeLevel = gradeLevel.split("-")[0];
            }
            if("K".equalsIgnoreCase(gradeLevel)) {
                return 0;
            }
            return Integer.valueOf(gradeLevel);
        }
    }
    
    public static void main(String args[]) throws Exception {
        CcsCsvReader englishLearningStandardReader = new CcsCsvReader();
        englishLearningStandardReader.setFileLocation(englishLearningStandardFile);
        englishLearningStandardReader.setContainsCopyright(true);
        englishLearningStandardReader.load();
        
        CcsCsv2XmlTransformer transformer = new CcsCsv2XmlTransformer();
        transformer.setCcsCsvReader(englishLearningStandardReader);
        transformer.setOutputLocation("data/InterchangeAssessmentMetadata-CCS-English.xml");
        transformer.setAcademicSubjectType(AcademicSubjectType.ENGLISH);
        
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
        transformer.setGradeLevelMapper(new EnglishGradeLevelMapper());
        // TODO: remove ignore when GUID is added to CSV
        transformer.setIgnoreNonExistentGuid(true);
        transformer.printLearningStandards();
        SchemaValidator.check(outputPath);
        System.out.println(transformer.getCopyright());
    }
}
