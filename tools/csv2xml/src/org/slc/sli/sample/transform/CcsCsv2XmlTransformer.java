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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slc.sli.sample.entitiesR1.AcademicSubjectType;
import org.slc.sli.sample.entitiesR1.ComplexObjectType;
import org.slc.sli.sample.entitiesR1.ContentStandardType;
import org.slc.sli.sample.entitiesR1.GradeLevelType;
import org.slc.sli.sample.entitiesR1.InterchangeAssessmentMetadata;
import org.slc.sli.sample.entitiesR1.LearningObjective;
import org.slc.sli.sample.entitiesR1.LearningStandard;
import org.slc.sli.sample.entitiesR1.LearningStandardId;

public class CcsCsv2XmlTransformer {
    private CcsCsvReader ccsCsvReader;
    private DotNotationToId dotNotationToId;
    private LearningObjectiveGenerator learningObjectiveGenerator;
    private GradeLevelMapper gradeLevelMapper;
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

    void setLearningObjectiveGenerator(LearningObjectiveGenerator learningObjectiveGenerator) {
        this.learningObjectiveGenerator = learningObjectiveGenerator;
    }
    
    void setGradeLevelMapper(GradeLevelMapper gradeLevelMapper) {
        this.gradeLevelMapper = gradeLevelMapper;
    }

    static abstract class DotNotationToId {
        abstract String getId(String dotNotation);
    }

    static abstract class LearningObjectiveGenerator {
        abstract Collection<LearningObjective> generateLearningObjectives(
                Map<String, Collection<LearningStandardResult>> learningObjectiveIdToLearningStandardResults,
                Map<String, String> idToGuidMap)
                throws IOException;
    }

    String getCopyright() {
        if(ccsCsvReader != null) {
            return ccsCsvReader.getCopyright();
        }
        return null;
    }


    private String getCopyrightXmlComment() {
        return "\n<!--\n\t"+ getCopyright().replace(">", "") + "\n-->";
    }

    /**
     * Iterate through common core standard csv records in the CSV files,
     * converts them into JAXB java objects, and then marshals them into SLI-EdFi xml file.
     *
     * @throws JAXBException
     */
    void printLearningStandards() throws JAXBException, IOException {
        Map<String, Collection<LearningStandardResult>> learningObjectiveIdToLearningStandardResults = new HashMap<String, Collection<LearningStandardResult>>();
        InterchangeAssessmentMetadata interchangeAssessmentMetadata = new InterchangeAssessmentMetadata();
        List<ComplexObjectType> learningStandards = interchangeAssessmentMetadata.getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();
        int learningStandardCounter = 0;
        while(ccsCsvReader.getCurrentRecord() != null) {
            LearningStandardResult learningStandardResult = getLearningStandard();
            if(learningStandardResult == null) {
                ccsCsvReader.getNextRecord();
                continue;
            }
            LearningStandard learningStandard = learningStandardResult.getLearningStandard();
            if(learningStandard != null) {
                learningStandards.add(learningStandard);

                String learningObjectiveId = learningStandardResult.getId();
                learningObjectiveId = learningObjectiveId.substring(0, learningObjectiveId.lastIndexOf('.'));
                if(learningObjectiveIdToLearningStandardResults.get(learningObjectiveId) == null) {
                    learningObjectiveIdToLearningStandardResults.put(learningObjectiveId, new ArrayList<LearningStandardResult>());
                }
                learningObjectiveIdToLearningStandardResults.get(learningObjectiveId).add(learningStandardResult);

                ccsCsvReader.getNextRecord();
                learningStandardCounter++;

            }
            else {
                ccsCsvReader.getNextRecord();
                continue;
            }
        }
        Collection<LearningObjective> learningObjectives = learningObjectiveGenerator.generateLearningObjectives(learningObjectiveIdToLearningStandardResults, IdToGuidMapper.getInstance().getIdToGuidMap());
        Collection<String> learningObjectiveIds = new HashSet<String>();
        Collection<String> learningObjectiveText = new HashSet<String>();
        for(LearningObjective learningObjective : learningObjectives) {
            if(learningObjective.getLearningObjectiveId().getIdentificationCode() != null)
                learningObjectiveIds.add(learningObjective.getLearningObjectiveId().getIdentificationCode().trim());
            if(learningObjective.getObjective() != null) {
                learningObjectiveText.add(learningObjective.getObjective().trim());
            }
        }
        learningStandards.addAll(learningObjectives);
//        JAXBContext context = JAXBContext.newInstance(LearningStandard.class);
        JAXBContext context = JAXBContext.newInstance(InterchangeAssessmentMetadata.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", getCopyrightXmlComment());
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(interchangeAssessmentMetadata, new PrintStream(new File(outputLocation), "UTF-8"));
        System.out.println("Total " + learningStandardCounter + " LearningStandards are exported.");
        System.out.println("Total " + learningObjectiveIdToLearningStandardResults.keySet().size() + " LearningObjectives are exported");
        System.out.println("Total Learning Objective IDs = " + learningObjectiveIds.size());
        System.out.println("Total Learning Objective Texts = " + learningObjectiveText.size());
    }
    
    GradeLevelType getGradeLevel(int intGradeLevel) {
        GradeLevelType gradeLevel;
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

    static class LearningStandardResult {
        private LearningStandard learningStandard;
        private String id;
        private String category;
        private String subCategory;

        public LearningStandard getLearningStandard() {
            return learningStandard;
        }

        public void setLearningStandard(LearningStandard learningStandard) {
            this.learningStandard = learningStandard;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
        
        private String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }
    }

    private LearningStandardResult getLearningStandard() throws IOException {
        Map<String, String> learningStandardRecord = ccsCsvReader.getCurrentRecord();
        String dotNotation = learningStandardRecord.get("ID");

        String id = dotNotationToId.getId(dotNotation);
        String guid = IdToGuidMapper.getInstance().getGuid(id);
        if(guid == null) {
            System.out.println("Dot Notation = <" + dotNotation + ">" + " ID = <" + id +
                    "> does not have guid. State Standard = <" + learningStandardRecord.get("State Standard") + ">");
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
        learningStandard.setGradeLevel(getGradeLevel(gradeLevelMapper.getGradeLevel(dotNotation)));
        learningStandard.setSubjectArea(academicSubjectType);

        LearningStandardResult learningStandardResult = new LearningStandardResult();
        learningStandardResult.setId(id);
        learningStandardResult.setLearningStandard(learningStandard);
        learningStandardResult.setCategory(learningStandardRecord.get("Category"));
        learningStandardResult.setSubCategory(learningStandardRecord.get("Sub Category"));
        return learningStandardResult;
    }

    static abstract class GradeLevelMapper {
        abstract int getGradeLevel(String dotNotation);
    }
    
    static class IdToGuidMapper {
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
                loadData();
            }
            return idToGuid.get(id);
        }

        Map<String, String> getIdToGuidMap() throws IOException {
            if (idToGuid == null) {
                loadData();
            }
            return idToGuid;
        }

        private void loadData() throws IOException {

                idToGuid = new HashMap<String, String>();
                CcsCsvReader reader = new CcsCsvReader();
                reader.setFileLocation(identifiersCSVFile);
                reader.load();

                while (reader.getCurrentRecord() != null) {
                    Map<String, String> currentRecord = reader.getCurrentRecord();
                    idToGuid.put(currentRecord.get("Dot notation"), currentRecord.get("GUID"));
                    reader.getNextRecord();
                }
        }
    }
}
