/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.bulk.extract.lea;

import java.io.File;
import java.security.PublicKey;
import java.util.Map;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

public class ExtractorFactory {
    
    public EdorgExtractor buildEdorgExtractor(EntityExtractor extractor, ExtractFileMap map, EdOrgExtractHelper edOrgExtractHelper) {
        return new EdorgExtractor(extractor, map, edOrgExtractHelper);
    }
    
    public StudentExtractor buildStudentExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentExtractor(extractor, map, repo, new ExtractorHelper(edOrgExtractHelper), new EntityToEdOrgCache(),
                new EntityToEdOrgCache(), edOrgExtractHelper);
    }
    
    public EntityDatedExtract buildStudentAssessmentExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentAssessmentExtractor(extractor, map, repo, edOrgExtractHelper);
    }
    
    public EntityDatedExtract buildYearlyTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new YearlyTranscriptExtractor(extractor, map, repo, edOrgExtractHelper, new EntityToEdOrgCache());
    }
    
    public EntityExtract buildParentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new ParentExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public StaffEdorgAssignmentExtractor buildStaffAssociationExtractor(EntityExtractor extractor,
            ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffEdorgAssignmentExtractor(extractor, map, repo, new ExtractorHelper(), new EntityToEdOrgCache(), edOrgExtractHelper);
    }
    
    public EntityExtract buildStaffExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffExtractor(extractor, map, repo, edOrgExtractHelper);
    }
    
    public EntityExtract buildTeacherSchoolExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new TeacherSchoolExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityExtract buildAttendanceExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EntityToEdOrgDateCache studentCache, EdOrgExtractHelper edOrgExtractHelper) {
        return new AttendanceExtractor(extractor, map, repo, new ExtractorHelper(), studentCache, edOrgExtractHelper);
    }
    
    public StudentSchoolAssociationExtractor buildStudentSchoolAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EntityToEdOrgDateCache studentCache, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentSchoolAssociationExtractor(extractor, map, repo, studentCache, edOrgExtractHelper);
    }
    
    public SessionExtractor buildSessionExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
    	return new SessionExtractor(extractor, map, repo, new ExtractorHelper(), new EntityToEdOrgCache(), edOrgExtractHelper);
    }
    
    public CalendarDateExtractor buildCalendarDateExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
    	return new CalendarDateExtractor(extractor, map, repo, new ExtractorHelper(), new EntityToEdOrgCache(), edOrgExtractHelper);
    }
    
    public EntityExtract buildGradingPeriodExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
    	return new GradingPeriodExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public ExtractFile buildLEAExtractFile(String path, String edOrg, String archiveName,
            Map<String, PublicKey> appPublicKeys, SecurityEventUtil securityEventUtil) {
        File directory = new File(path, edOrg);
        directory.mkdirs();
        return new ExtractFile(directory, archiveName, appPublicKeys, securityEventUtil);
    }
    
    public EntityExtract buildStaffCohortAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffCohortAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public SectionExtractor buildSectionExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository, EntityToEdOrgCache entityCache, EntityToEdOrgCache edorgCache, EdOrgExtractHelper edOrgExtractHelper) {
        return new SectionExtractor(entityExtractor, extractFileMap, repository, entityCache, edorgCache, edOrgExtractHelper);
    }
    
    public EntityExtract buildStaffProgramAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffProgramAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }
    
    public CourseExtractor buildCourseExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new CourseExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public CourseOfferingExtractor buildCourseOfferingExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new CourseOfferingExtractor(extractor, map, repo, edOrgExtractHelper);
    }
    
    public CourseTranscriptExtractor buildCourseTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo) {
        return new CourseTranscriptExtractor(extractor, map, repo);
    }
    
    public EntityExtract buildStudentGradebookEntryExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentGradebookEntryExtractor(extractor, map, repo, edOrgExtractHelper);
    }
    
    public EntityExtract buildStudentCompetencyExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository) {
    	return new StudentCompetencyExtractor(entityExtractor, extractFileMap, repository);
    }

    public GraduationPlanExtractor buildGraduationPlanExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new GraduationPlanExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildDisciplineExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository, EntityToEdOrgCache edorgCache, EntityToEdOrgDateCache entityCache) {
        return new DisciplineExtractor(entityExtractor, extractFileMap, repository, entityCache, edorgCache);
    }
}
