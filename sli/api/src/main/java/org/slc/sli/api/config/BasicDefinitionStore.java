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

package org.slc.sli.api.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.ReferenceSchema;

/**
 * Default implementation of the entity definition store
 *
 * Instructions on adding entities:
 *
 * If the entity that needs to be exposed to the api is identical to something in the database, most
 * of the defaults should work for you.
 * factory.makeEntity(nameOfEntityType, pathInURI).buildAndRegister(this);
 *
 * If your entity requires some processing when it is fetched from the db and stored back in, this
 * can be handled by adding one or more treatments
 * factory.makeEntity(...).withTreatments(firstTransformation,
 * anotherTransformation).buildAndRegister(this);
 *
 * If it needs to be stored in a collection other than the one named by the entity type (if it is
 * sharing a collection with another type):
 * factory.makeEntity(...).storeAs(collectionName).buildAndRegister(this);
 *
 * If it needs to be stored somewhere other than the default db (for instance if this is something
 * that should be kept in memory), define your own repo and use with this:
 * factory.makeEntity(...).storeIn(newRepo).buildAndRegister(this);
 *
 *
 * @author nbrown
 *
 */
@Component
public class BasicDefinitionStore implements EntityDefinitionStore {

    private Map<String, EntityDefinition> mapping = new HashMap<String, EntityDefinition>();

    @Autowired
    private DefinitionFactory factory;

    @Autowired
    private SchemaRepository repo;

    @Autowired
    @Qualifier("searchRepo")
    private Repository<Entity> searchRepo;

    @Override
    public EntityDefinition lookupByResourceName(String resourceName) {
        return mapping.get(resourceName);
    }

    @Override
    public EntityDefinition lookupByEntityType(String entityType) {
        for (Map.Entry<String, EntityDefinition> e : mapping.entrySet()) {
            if (e.getValue().getType().equals(entityType)) {
                return e.getValue();
            }
        }

        return null;
    }

    @Override
    public Collection<EntityDefinition> getLinked(EntityDefinition defn) {
        return defn.getReferencingEntities();
    }

    @PostConstruct
    @Autowired
    public void init() {

        // adding the entity definitions
        EntityDefinition assessment = factory.makeEntity(EntityNames.ASSESSMENT, ResourceNames.ASSESSMENTS)
                .buildAndRegister(this);
        factory.makeEntity(EntityNames.ATTENDANCE, ResourceNames.ATTENDANCES)
                .buildAndRegister(this);
        // factory.makeEntity(EntityNames.BELL_SCHEDULE,
        // ResourceNames.BELL_SCHEDULES).buildAndRegister(this);
        EntityDefinition cohort = factory.makeEntity(EntityNames.COHORT, ResourceNames.COHORTS).supportsAggregates()
                .buildAndRegister(this);
        EntityDefinition course = factory.makeEntity(EntityNames.COURSE, ResourceNames.COURSES).buildAndRegister(this);
        EntityDefinition studentCompetencyObjective = factory.makeEntity(EntityNames.STUDENT_COMPETENCY_OBJECTIVE,
                ResourceNames.STUDENT_COMPETENCY_OBJECTIVES).buildAndRegister(this);
        factory.makeEntity(EntityNames.COMPETENCY_LEVEL_DESCRIPTOR, ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS)
                .buildAndRegister(this);
        EntityDefinition disciplineIncident = factory.makeEntity(EntityNames.DISCIPLINE_INCIDENT,
                ResourceNames.DISCIPLINE_INCIDENTS).buildAndRegister(this);
        factory.makeEntity(EntityNames.DISCIPLINE_ACTION, ResourceNames.DISCIPLINE_ACTIONS).buildAndRegister(this);
        EntityDefinition educationOrganization = factory.makeEntity(EntityNames.EDUCATION_ORGANIZATION,
                ResourceNames.EDUCATION_ORGANIZATIONS).buildAndRegister(this);
        factory.makeEntity(EntityNames.GRADEBOOK_ENTRY, ResourceNames.GRADEBOOK_ENTRIES).buildAndRegister(this);
        EntityDefinition program = factory.makeEntity(EntityNames.PROGRAM, ResourceNames.PROGRAMS).buildAndRegister(
                this);
        EntityDefinition school = factory.makeEntity(EntityNames.SCHOOL, ResourceNames.SCHOOLS)
                .storeAs(EntityNames.EDUCATION_ORGANIZATION).supportsAggregates().buildAndRegister(this);
        EntityDefinition section = factory.makeEntity(EntityNames.SECTION, ResourceNames.SECTIONS).supportsAggregates()
                .buildAndRegister(this);
        EntityDefinition session = factory.makeEntity(EntityNames.SESSION, ResourceNames.SESSIONS).supportsAggregates()
                .buildAndRegister(this);
        EntityDefinition staff = factory.makeEntity(EntityNames.STAFF, ResourceNames.STAFF).buildAndRegister(this);
        EntityDefinition student = factory.makeEntity(EntityNames.STUDENT, ResourceNames.STUDENTS).buildAndRegister(
                this);
        factory.makeEntity(EntityNames.STUDENT_GRADEBOOK_ENTRY, ResourceNames.STUDENT_GRADEBOOK_ENTRIES)
                .buildAndRegister(this);
        EntityDefinition teacher = factory.makeEntity(EntityNames.TEACHER, ResourceNames.TEACHERS)
                .storeAs(EntityNames.STAFF).buildAndRegister(this);
        EntityDefinition parent = factory.makeEntity(EntityNames.PARENT, ResourceNames.PARENTS).buildAndRegister(this);
        factory.makeEntity(EntityNames.STUDENT_ACADEMIC_RECORD, ResourceNames.STUDENT_ACADEMIC_RECORDS)
                .buildAndRegister(this);

        factory.makeEntity(EntityNames.AGGREGATION, ResourceNames.AGGREGATIONS).buildAndRegister(this);
        factory.makeEntity(EntityNames.AGGREGATION_DEFINITION, ResourceNames.AGGREGATION_DEFINITIONS).buildAndRegister(
                this);

        factory.makeEntity(EntityNames.LEARNING_OBJECTIVE, ResourceNames.LEARNINGOBJECTIVES).buildAndRegister(this);
        factory.makeEntity(EntityNames.LEARNING_STANDARD, ResourceNames.LEARNINGSTANDARDS).buildAndRegister(this);
        factory.makeEntity(EntityNames.GRADE, ResourceNames.GRADES).buildAndRegister(this);
        factory.makeEntity(EntityNames.STUDENT_COMPETENCY, ResourceNames.STUDENT_COMPETENCIES).buildAndRegister(this);
        factory.makeEntity(EntityNames.GRADING_PERIOD, ResourceNames.GRADING_PERIODS).buildAndRegister(this);
        factory.makeEntity(EntityNames.REPORT_CARD, ResourceNames.REPORT_CARDS).buildAndRegister(this);
        factory.makeEntity(EntityNames.ADMIN_DELEGATION, ResourceNames.ADMIN_DELEGATION).buildAndRegister(this);
        factory.makeEntity(EntityNames.SEARCH, ResourceNames.SEARCH).storeIn(searchRepo).skipContextValidation()
                .wrapperEntity().buildAndRegister(this);
        factory.makeEntity(EntityNames.GRADUATION_PLAN, ResourceNames.GRADUATION_PLANS).buildAndRegister(this);

        // adding the association definitions
        AssociationDefinition studentSchoolAssociation = factory
                .makeAssoc("studentSchoolAssociation", "studentSchoolAssociations")
                .exposeAs(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS).storeAs("studentSchoolAssociation")
                .from(student, "getStudent", "getStudents").to(school, "getSchool", "getSchools")
                .calledFromSource("getStudentSchoolAssociations").calledFromTarget("getStudentSchoolAssociations")
                .build();
        addDefinition(studentSchoolAssociation);

        AssociationDefinition teacherSectionAssociation = factory
                .makeAssoc("teacherSectionAssociation", "teacherSectionAssociations")
                .exposeAs(ResourceNames.TEACHER_SECTION_ASSOCIATIONS).storeAs("teacherSectionAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(section, "getSection", "getSections")
                .calledFromSource("getTeacherSectionAssociations").calledFromTarget("getTeacherSectionAssociations")
                .build();
        addDefinition(teacherSectionAssociation);

        AssociationDefinition studentAssessment = factory.makeAssoc("studentAssessment", "studentAssessments")
                .exposeAs(ResourceNames.STUDENT_ASSESSMENTS).storeAs("studentAssessment")
                .from(student, "getStudent", null).to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getStudentAssessments").calledFromTarget("getStudentAssessments").build();
        addDefinition(studentAssessment);

        AssociationDefinition studentSectionAssociation = factory
                .makeAssoc("studentSectionAssociation", "studentSectionAssociations")
                .exposeAs(ResourceNames.STUDENT_SECTION_ASSOCIATIONS).storeAs("studentSectionAssociation")
                .from(student, "getStudent", "getStudents").to(section, "getSection", "getSections")
                .calledFromSource("getStudentSectionAssociations").calledFromTarget("getStudentSectionAssociations")
                .build();
        addDefinition(studentSectionAssociation);

        AssociationDefinition teacherSchoolAssociation = factory
                .makeAssoc("teacherSchoolAssociation", "teacherSchoolAssociations")
                .exposeAs(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS).storeAs("teacherSchoolAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(school, "getSchool", "getSchools")
                .calledFromSource("getTeacherSchoolAssociations").calledFromTarget("getTeacherSchoolAssociations")
                .build();
        addDefinition(teacherSchoolAssociation);

        AssociationDefinition staffEducationOrgAssignmentAssociation = factory
                .makeAssoc("staffEducationOrganizationAssociation", "staffEducationOrgAssignmentAssociations")
                .exposeAs(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)
                .storeAs("staffEducationOrganizationAssociation")
                .from(staff, "getStaff", "getStaff", "staffReference")
                .to(educationOrganization, "getEducationOrganization", "getEducationOrganizations",
                        "educationOrganizationReference")
                .calledFromSource("getStaffEducationOrgAssignmentAssociations")
                .calledFromTarget("getStaffEducationOrgAssignmentAssociations").build();
        addDefinition(staffEducationOrgAssignmentAssociation);

        AssociationDefinition educationOrganizationAssociation = factory
                .makeAssoc("educationOrganizationAssociation", "educationOrganizationAssociations")
                .exposeAs("educationOrganizationAssociations")
                .storeAs("educationOrganizationAssociation")
                .from(educationOrganization, "getEducationOrganizationParent", "getEducationOrganizationParents",
                        "educationOrganizationParentId")
                .to(educationOrganization, "getEducationOrganizationChild", "getEducationOrganizationChilds",
                        "educationOrganizationChildId").calledFromSource("getEducationOrganizationAssociations")
                .calledFromTarget("getEducationOrganizationAssociations").build();
        addDefinition(educationOrganizationAssociation);

        AssociationDefinition courseOffering = factory.makeAssoc("courseOffering", "courseOfferings")
                .exposeAs(ResourceNames.COURSE_OFFERINGS).storeAs("courseOffering")
                .from(session, "getSession", "getSessions").to(course, "getCourse", "getCourses")
                .calledFromSource("getCourseOfferings").calledFromTarget("getCourseOfferings").build();
        addDefinition(courseOffering);

        AssociationDefinition courseTranscript = factory.makeAssoc("courseTranscript", "courseTranscripts")
                .exposeAs(ResourceNames.COURSE_TRANSCRIPTS).storeAs("courseTranscript")
                .from(student, "getStudent", null).to(course, "getCourse", "getCourses")
                .calledFromSource("getCourseTranscripts").calledFromTarget("getCourseTranscripts").build();
        addDefinition(courseTranscript);

        AssociationDefinition studentParentAssociation = factory
                .makeAssoc(EntityNames.STUDENT_PARENT_ASSOCIATION, "studentParentAssociations")
                .exposeAs(ResourceNames.STUDENT_PARENT_ASSOCIATIONS).storeAs(EntityNames.STUDENT_PARENT_ASSOCIATION)
                .from(student, "getStudent", "getStudents").to(parent, "getParent", "getParents")
                .calledFromSource("getStudentParentAssociations").calledFromTarget("getStudentParentAssociations")
                .build();
        addDefinition(studentParentAssociation);

        AssociationDefinition studentDisciplineIncidentAssociation = factory
                .makeAssoc(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, "studentDisciplineIncidentAssociations")
                .exposeAs(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS)
                .storeAs(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION)
                .from(student, "getStudent", "getStudents")
                .to(disciplineIncident, "getDisciplineIncident", "getDisciplineIncidents")
                .calledFromSource("getStudentDisciplineIncidentAssociations")
                .calledFromTarget("getStudentDisciplineIncidentAssociations").build();
        addDefinition(studentDisciplineIncidentAssociation);

        AssociationDefinition studentProgramAssociation = factory
                .makeAssoc(EntityNames.STUDENT_PROGRAM_ASSOCIATION, "studentProgramAssociations")
                .exposeAs(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS).storeAs(EntityNames.STUDENT_PROGRAM_ASSOCIATION)
                .from(student, "getStudent", "getStudents").to(program, "getProgram", "getPrograms")
                .calledFromSource("getStudentProgramAssociations").calledFromTarget("getStudentProgramAssociations")
                .build();
        addDefinition(studentProgramAssociation);

        AssociationDefinition staffProgramAssociation = factory
                .makeAssoc(EntityNames.STAFF_PROGRAM_ASSOCIATION, "staffProgramAssociations")
                .exposeAs(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS).storeAs(EntityNames.STAFF_PROGRAM_ASSOCIATION)
                .from(staff, "getStaff", "getStaff").to(program, "getProgram", "getPrograms")
                .calledFromSource("getStaffProgramAssociations").calledFromTarget("getStaffProgramAssociations")
                .build();
        addDefinition(staffProgramAssociation);

        AssociationDefinition studentCohortAssociation = factory
                .makeAssoc(EntityNames.STUDENT_COHORT_ASSOCIATION, "studentCohortAssociations")
                .exposeAs(ResourceNames.STUDENT_COHORT_ASSOCIATIONS).storeAs(EntityNames.STUDENT_COHORT_ASSOCIATION)
                .from(student, "getStudent", "getStudents").to(cohort, ResourceNames.COHORT_GETTER, "getCohorts")
                .calledFromSource(ResourceNames.STUDENT_COHORT_ASSOCIATIONS_GETTER)
                .calledFromTarget(ResourceNames.STUDENT_COHORT_ASSOCIATIONS_GETTER).build();
        addDefinition(studentCohortAssociation);

        AssociationDefinition staffCohortAssociation = factory
                .makeAssoc(EntityNames.STAFF_COHORT_ASSOCIATION, "staffCohortAssociations")
                .exposeAs(ResourceNames.STAFF_COHORT_ASSOCIATIONS).storeAs(EntityNames.STAFF_COHORT_ASSOCIATION)
                .from(staff, "getStaff", "getStaff").to(cohort, ResourceNames.COHORT_GETTER, "getCohorts")
                .calledFromSource(ResourceNames.STAFF_COHORT_ASSOCIATIONS_GETTER)
                .calledFromTarget(ResourceNames.STAFF_COHORT_ASSOCIATIONS_GETTER).build();
        addDefinition(staffCohortAssociation);

        // Adding the security collection
        EntityDefinition roles = factory.makeEntity("roles").storeAs("roles").build();
        addDefinition(roles);
        addDefinition(factory.makeEntity("realm").storeAs("realm").build());
        addDefinition(factory.makeEntity("authSession").build());
        addDefinition(factory.makeEntity("customRole").storeAs("customRole").build());

        // Adding the application collection
        addDefinition(factory.makeEntity("application", "apps").storeAs("application").build());
        addDefinition(factory.makeEntity("applicationAuthorization").storeAs("applicationAuthorization").build());

        addDefinition(factory.makeEntity("tenant", "tenants").storeAs("tenant").build());
        addDefinition(factory.makeEntity("securityEvent").storeAs("securityEvent").build());

        this.registerDirectReferences();
    }

    /**
     * done last to avoid existence issues
     *
     */
    private void registerDirectReferences() {

        //
        debug("Registering direct entity references");

        int referencesLoaded = 0;

        // loop for each entity that is defined
        for (EntityDefinition referringDefinition : this.mapping.values()) {

            // loop for each reference field on the entity
            for (Entry<String, ReferenceSchema> fieldSchema : referringDefinition.getReferenceFields().entrySet()) {
                ReferenceSchema schema = fieldSchema.getValue(); // access to the reference schema
                Set<String> resources = ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.get(schema.getEntityType());

                if (resources == null) {
                    continue;
                }
                for (String resource : resources) {
                    EntityDefinition referencedEntity = mapping.get(resource);
                    debug("* New reference: {}.{} -> {}._id",
                            new Object[] { referringDefinition.getStoredCollectionName(), fieldSchema.getKey(),
                                    schema.getEntityType() });
                    // tell the referenced entity that some entity definition refers to it

                    if (referencedEntity != null) {
                        referencedEntity.addReferencingEntity(referringDefinition);
                        referencesLoaded++;
                    } else {
                        warn("* Failed to add, null entity: {}.{} -> {}._id",
                                new Object[] { referringDefinition.getStoredCollectionName(), fieldSchema.getKey(),
                                        schema.getEntityType() });
                    }
                }
            }
        }

        // print stats
        debug("{} direct references loaded.", referencesLoaded);
    }

    public void addDefinition(EntityDefinition defn) {
        debug("adding definition for {}", defn.getResourceName());
        NeutralSchema schema = repo.getSchema(defn.getStoredCollectionName());
        defn.setSchema(schema);

        if (ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.containsKey(defn.getStoredCollectionName())) {
            ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.get(defn.getStoredCollectionName()).add(defn.getResourceName());
        } else {
            Set<String> list = new HashSet<String>();
            list.add(defn.getResourceName());
            ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.put(defn.getStoredCollectionName(), list);
        }
        mapping.put(defn.getResourceName(), defn);
    }
}
