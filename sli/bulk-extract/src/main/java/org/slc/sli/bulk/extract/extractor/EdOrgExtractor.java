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
package org.slc.sli.bulk.extract.extractor;

import static org.slc.sli.bulk.extract.LogUtil.audit;

import java.io.File;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.lea.EntityDatedExtract;
import org.slc.sli.bulk.extract.lea.EntityExtract;
import org.slc.sli.bulk.extract.lea.EntityToEdOrgDateCache;
import org.slc.sli.bulk.extract.lea.ExtractFileMap;
import org.slc.sli.bulk.extract.lea.ExtractorFactory;
import org.slc.sli.bulk.extract.lea.SectionEmbeddedDocsExtractor;
import org.slc.sli.bulk.extract.lea.StaffEdorgAssignmentExtractor;
import org.slc.sli.bulk.extract.lea.StudentExtractor;
import org.slc.sli.bulk.extract.lea.StudentGradebookEntryExtractor;
import org.slc.sli.bulk.extract.lea.YearlyTranscriptExtractor;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Creates top-level ed org tarballs.
 */
public class EdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(EdOrgExtractor.class);
    private Repository<Entity> repository;

    @Autowired
    private EdOrgExtractHelper helper;

    private ExtractFileMap edOrgToExtractFileMap;
    private EntityExtractor entityExtractor;
    private Map<String, String> entitiesToCollections;
    private BulkExtractMongoDA bulkExtractMongoDA;
    private ExtractorFactory factory;

    @Autowired
    private SecurityEventUtil securityEventUtil;

    private File tenantDirectory;
    private DateTime startTime;

    /**
     * Creates encrypted edOrg bulk extract files for the given tenant.
     *
     * @param tenant - name of tenant to extract
     * @param tenantDirectory - name of directory into which to extract
     * @param startTime - Start time of extract
     */
    public void execute(String tenant, File tenantDirectory, DateTime startTime) {
        // 1. SETUP
        TenantContext.setTenantId(tenant);
        this.tenantDirectory = tenantDirectory;
        this.startTime = startTime;

        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(), "Top-level extract initiated", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0008));

        if (factory == null) {
            factory = new ExtractorFactory();
        }
        if (edOrgToExtractFileMap == null) {
            edOrgToExtractFileMap = new ExtractFileMap(buildEdOrgToExtractFile());
        }

        // 2. EXTRACT
        // Student
        StudentExtractor student = factory.buildStudentExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        student.extractEntities(null);

        EntityDatedExtract attendanceExtractor = factory.buildAttendanceExtractor(entityExtractor, edOrgToExtractFileMap,
                repository, helper);
        attendanceExtractor.extractEntities(student.getStudentDatedCache());

        EntityDatedExtract studentSchoolAssociation = factory.buildStudentSchoolAssociationExtractor(entityExtractor,
                edOrgToExtractFileMap, repository, helper);
        studentSchoolAssociation.extractEntities(student.getStudentDatedCache());

        EntityDatedExtract studentAssessmentExtractor = factory.buildStudentAssessmentExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        studentAssessmentExtractor.extractEntities(student.getStudentDatedCache());

        StudentGradebookEntryExtractor studentGradebookExtractor = factory.buildStudentGradebookEntryExtractor(entityExtractor, edOrgToExtractFileMap,
                repository, helper);
        studentGradebookExtractor.extractEntities(student.getStudentDatedCache());

        // Discipline
        EntityDatedExtract discipline = factory.buildDisciplineExtractor(entityExtractor, edOrgToExtractFileMap, repository, student.getStudentDatedCache());
        discipline.extractEntities(student.getDiDateCache());

        // Yearly Transcript
        YearlyTranscriptExtractor yearlyTranscript = factory.buildYearlyTranscriptExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        yearlyTranscript.extractEntities(student.getStudentDatedCache());
        EntityToEdOrgDateCache studentAcademicRecordDateCache = yearlyTranscript.getStudentAcademicRecordDateCache();

        // Course Transcript
        EntityDatedExtract courseTranscriptExtractor = factory.buildCourseTranscriptExtractor(entityExtractor, edOrgToExtractFileMap, repository,
                student.getStudentDatedCache());
        courseTranscriptExtractor.extractEntities(studentAcademicRecordDateCache);

        EntityExtract genericExtractor = factory.buildParentExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        genericExtractor.extractEntities(student.getParentCache());

        // Staff
        StaffEdorgAssignmentExtractor seoaExtractor = factory.buildStaffAssociationExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        seoaExtractor.extractEntities(null);

        EntityDatedExtract staffExtractor = factory.buildStaffExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        staffExtractor.extractEntities(seoaExtractor.getStaffDatedCache());

        EntityDatedExtract teacherSchoolAssociationExtractor = factory.buildTeacherSchoolAssociationExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        teacherSchoolAssociationExtractor.extractEntities(seoaExtractor.getStaffDatedCache());

        EntityDatedExtract staffProgramExtractor = factory.buildStaffProgramAssociationExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        staffProgramExtractor.extractEntities(seoaExtractor.getStaffDatedCache());

        EntityDatedExtract staffCohortExtractor = factory.buildStaffCohortAssociationExtractor(entityExtractor, edOrgToExtractFileMap, repository, helper);
        staffCohortExtractor.extractEntities(seoaExtractor.getStaffDatedCache());

        // Section
        SectionEmbeddedDocsExtractor sectionExtractor = factory.buildSectionExtractor(entityExtractor, edOrgToExtractFileMap, repository, student.getStudentDatedCache(),
                helper, seoaExtractor.getStaffDatedCache());
        sectionExtractor.extractEntities(studentGradebookExtractor.getGradebookEntryCache());

        EntityDatedExtract studentCompetencyExtractor = factory.buildStudentCompetencyExtractor(entityExtractor, edOrgToExtractFileMap, repository);
        studentCompetencyExtractor.extractEntities(sectionExtractor.getStudentSectionAssociationDateCache());

        edOrgToExtractFileMap.closeFiles();

        edOrgToExtractFileMap.buildManifestFiles(startTime);
        edOrgToExtractFileMap.archiveFiles();

        // 3. ARCHIVE
        updateBulkExtractDb(tenant, startTime);
        LOG.info("Finished top-level extract in: {} seconds",
                (new DateTime().getMillis() - this.startTime.getMillis()) / 1000);
        audit(securityEventUtil.createSecurityEvent(this.getClass().getName(), "Marks the end of top-level extract", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0009));
    }

    private void updateBulkExtractDb(String tenant, DateTime startTime) {
        for (String edOrg : helper.getBulkExtractEdOrgs()) {
            // update db to point to new archive
            for (Entry<String, File> archiveFile : edOrgToExtractFileMap.getExtractFileForEdOrg(edOrg).getArchiveFiles()
                    .entrySet()) {
                bulkExtractMongoDA.updateDBRecord(tenant, archiveFile.getValue().getAbsolutePath(), archiveFile.getKey(),
                        startTime.toDate(), false, edOrg, false);
            }
        }
    }

    private Map<String, ExtractFile> buildEdOrgToExtractFile() {
        Map<String, ExtractFile> edOrgToExtractFile = new HashMap<String, ExtractFile>();

        Map<String, PublicKey> appPublicKeys = bulkExtractMongoDA.getAppPublicKeys();
        for (String edOrg : helper.getBulkExtractEdOrgs()) {
            ExtractFile file = factory.buildEdOrgExtractFile(tenantDirectory.getAbsolutePath(), edOrg,
                    getArchiveName(edOrg, startTime.toDate()), appPublicKeys, securityEventUtil);
            edOrgToExtractFile.put(edOrg, file);
        }
        return edOrgToExtractFile;
    }

    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

    private String getArchiveName(String edOrg, Date startTime) {
        return edOrg + "-" + Launcher.getTimeStamp(startTime);
    }

    public void setEntityExtractor(EntityExtractor entityExtractor) {
        this.entityExtractor = entityExtractor;
    }

    public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
        this.entitiesToCollections = entitiesToCollections;
    }

    /**
     * Set bulkExtractMongoDA.
     *
     * @param bulkExtractMongoDA
     *            the bulkExtractMongoDA to set
     */
    public void setBulkExtractMongoDA(BulkExtractMongoDA bulkExtractMongoDA) {
        this.bulkExtractMongoDA = bulkExtractMongoDA;
    }

    public void setHelper(EdOrgExtractHelper helper) {
        this.helper = helper;
    }

    public void setFactory(ExtractorFactory factory) {
        this.factory = factory;
    }

    public void setEdOrgToExtractMap(ExtractFileMap map) {
        this.edOrgToExtractFileMap = map;
    }

}
