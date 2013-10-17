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
package org.slc.sli.bulk.extract.lea;

import com.google.common.base.Predicate;
import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.extractor.LocalEdOrgExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dkornishev
 */
public class SectionEmbeddedDocsExtractor implements EntityDatedExtract {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);

    private final EntityExtractor entityExtractor;
    private final ExtractFileMap leaToExtractFileMap;
    private final Repository<Entity> repository;
    private final EntityToEdOrgDateCache studentDatedCache;
    private final EntityToEdOrgCache edorgCache;
    private final EntityToEdOrgDateCache studentSectionAssociationDateCache = new EntityToEdOrgDateCache();

    public SectionEmbeddedDocsExtractor(EntityExtractor entityExtractor, ExtractFileMap leaToExtractFileMap, Repository<Entity> repository, EntityToEdOrgDateCache studentCache, EntityToEdOrgCache edorgCache, EdOrgExtractHelper edOrgExtractHelper) {
        this.entityExtractor = entityExtractor;
        this.leaToExtractFileMap = leaToExtractFileMap;
        this.repository = repository;
        this.edorgCache = edorgCache;

        this.studentDatedCache = studentCache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void extractEntities(final EntityToEdOrgDateCache gradebookEntryCache) {
        Iterator<Entity> sections = this.repository.findEach(EntityNames.SECTION, new NeutralQuery());

        while (sections.hasNext()) {
            Entity section = sections.next();

            extractTeacherSectionAssociation(section);
            extractGradebookEntry(section, gradebookEntryCache);
            extractStudentSectionAssociation(section);
        }
    }

    private void extractTeacherSectionAssociation(Entity section) {
        //Extract teacherSectionAssociations based on the schoolId of the Section
        String schoolId = (String) section.getBody().get(ParameterConstants.SCHOOL_ID);
        final Set<String> allEdOrgs = this.edorgCache.ancestorEdorgs(schoolId);

        if (null != allEdOrgs && allEdOrgs.size() != 0) {  // Edorgs way
            for(final String edOrg: allEdOrgs) {
                this.entityExtractor.extractEmbeddedEntities(section, this.leaToExtractFileMap.getExtractFileForEdOrg(edOrg), EntityNames.SECTION,
                        new Predicate<Entity>() {
                    @Override
                    public boolean apply(Entity input) {
                        boolean shouldExtract = false;
                        if (input.getType().equals(EntityNames.TEACHER_SECTION_ASSOCIATION))    {
                            shouldExtract = true;
                        }
                        return shouldExtract;
                    }
                });
            }
        }
    }

    private void extractStudentSectionAssociation(Entity section) {
        //Extract studentSectionAssociation based on the edOrgs of the student
        List<Entity> ssas = section.getEmbeddedData().get(EntityNames.STUDENT_SECTION_ASSOCIATION);

        if (ssas != null) {
            for (Entity ssa : ssas) {
                String studentId = (String) ssa.getBody().get(ParameterConstants.STUDENT_ID);
                Map<String, DateTime> studentEdOrgs = studentDatedCache.getEntriesById(studentId);

                for (Map.Entry<String, DateTime> studentEdOrg : studentEdOrgs.entrySet()) {
                    if (EntityDateHelper.shouldExtract(ssa, studentEdOrg.getValue())) {
                        entityExtractor.extractEntity(ssa, this.leaToExtractFileMap.getExtractFileForEdOrg(studentEdOrg.getKey()), EntityNames.STUDENT_SECTION_ASSOCIATION);

                        this.studentSectionAssociationDateCache.addEntry(ssa.getEntityId(), studentEdOrg.getKey(), studentEdOrg.getValue());
                    }
                }
            }
        }
    }

    private void extractGradebookEntry(Entity section, EntityToEdOrgDateCache gradebookEntryCache) {
        List<Entity> gradebookEntries = section.getEmbeddedData().get(EntityNames.GRADEBOOK_ENTRY);

        if (gradebookEntries != null) {
            for (Entity gradebookEntry : gradebookEntries) {
                String entityId = gradebookEntry.getEntityId();
                Map<String, DateTime> gradebookEntryEdOrgs = gradebookEntryCache.getEntriesById(entityId);

                for (Map.Entry<String, DateTime> entry : gradebookEntryEdOrgs.entrySet()) {
                    DateTime upToDate = entry.getValue();
                    if (EntityDateHelper.shouldExtract(gradebookEntry, upToDate)) {
                        entityExtractor.extractEntity(gradebookEntry, this.leaToExtractFileMap.getExtractFileForEdOrg(entry.getKey()), EntityNames.GRADEBOOK_ENTRY);
                    }
                }
            }
        }
    }

    public EntityToEdOrgDateCache getStudentSectionAssociationDateCache() {
        return studentSectionAssociationDateCache;
    }

}
