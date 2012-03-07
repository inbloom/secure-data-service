package org.slc.sli.api.config;

import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ReferenceSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    private static final Logger LOG = LoggerFactory.getLogger(BasicDefinitionStore.class);
    
    private Map<String, EntityDefinition> mapping = new HashMap<String, EntityDefinition>();
    
    @Autowired
    private DefinitionFactory factory;
    
    @Autowired
    private SchemaRepository repo;
    
    @Override
    public EntityDefinition lookupByResourceName(String resourceName) {
        return mapping.get(resourceName);
    }
    
    @Override
    public EntityDefinition lookupByEntityType(String entityType) {
        return mapping.get(ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.get(entityType));
    }
    
    @Override
    public Collection<EntityDefinition> getLinked(EntityDefinition defn) {
        return defn.getReferencingEntities();
    }

    @PostConstruct
    @Autowired
    public void init() {
        
        // adding the entity definitions
        EntityDefinition assessment = factory.makeEntity(EntityNames.ASSESSMENT, ResourceNames.ASSESSMENTS).buildAndRegister(this);
        factory.makeEntity(EntityNames.ATTENDANCE, ResourceNames.ATTENDANCES).buildAndRegister(this);
        factory.makeEntity(EntityNames.BELL_SCHEDULE, ResourceNames.BELL_SCHEDULES).buildAndRegister(this);
        factory.makeEntity(EntityNames.COHORT, ResourceNames.COHORTS).buildAndRegister(this);
        EntityDefinition course = factory.makeEntity(EntityNames.COURSE, ResourceNames.COURSES).buildAndRegister(this);
        factory.makeEntity(EntityNames.DISCIPLINE_INCIDENT, ResourceNames.DISCIPLINE_INCIDENTS).buildAndRegister(this);
        EntityDefinition educationOrganization = factory.makeEntity(EntityNames.EDUCATION_ORGANIZATION, ResourceNames.EDUCATION_ORGANIZATIONS)
                .buildAndRegister(this);
        factory.makeEntity(EntityNames.PARENT, ResourceNames.PARENTS).buildAndRegister(this);
        factory.makeEntity(EntityNames.PROGRAM, ResourceNames.PROGRAMS).buildAndRegister(this);
        EntityDefinition school = factory.makeEntity(EntityNames.SCHOOL, ResourceNames.SCHOOLS).buildAndRegister(this);
        EntityDefinition section = factory.makeEntity(EntityNames.SECTION, ResourceNames.SECTIONS).buildAndRegister(this);
        EntityDefinition session = factory.makeEntity(EntityNames.SESSION, ResourceNames.SESSIONS).buildAndRegister(this);
        EntityDefinition staff = factory.makeEntity(EntityNames.STAFF, ResourceNames.STAFF).buildAndRegister(this);
        EntityDefinition student = factory.makeEntity(EntityNames.STUDENT, ResourceNames.STUDENTS).buildAndRegister(this);
        EntityDefinition teacher = factory.makeEntity(EntityNames.TEACHER, ResourceNames.TEACHERS).buildAndRegister(this);

        factory.makeEntity(EntityNames.AGGREGATION, ResourceNames.AGGREGATIONS).buildAndRegister(this);
        factory.makeEntity(EntityNames.AGGREGATION_DEFINITION, ResourceNames.AGGREGATION_DEFINITIONS).buildAndRegister(this);

        // adding the association definitions
        AssociationDefinition studentSchoolAssociation = factory.makeAssoc("studentSchoolAssociation")
                .exposeAs(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS).storeAs("studentSchoolAssociation")
                .from(student, "getStudent", "getStudents").to(school, "getSchool", "getSchools")
                .calledFromSource("getStudentSchoolAssociations").calledFromTarget("getStudentSchoolAssociations")
                .build();
        addDefinition(studentSchoolAssociation);
        
        AssociationDefinition teacherSectionAssociation = factory.makeAssoc("teacherSectionAssociation")
                .exposeAs(ResourceNames.TEACHER_SECTION_ASSOCIATIONS).storeAs("teacherSectionAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(section, "getSection", "getSections")
                .calledFromSource("getTeacherSectionAssociations").calledFromTarget("getTeacherSectionAssociations")
                .build();
        addDefinition(teacherSectionAssociation);
        
        AssociationDefinition studentAssessmentAssociation = factory.makeAssoc("studentAssessmentAssociation")
                .exposeAs(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS).storeAs("studentAssessmentAssociation")
                .from(student, "getStudent", "getStudents").to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getStudentAssessmentAssociations")
                .calledFromTarget("getStudentAssessmentAssociations").build();
        addDefinition(studentAssessmentAssociation);
        
        AssociationDefinition studentSectionAssociation = factory.makeAssoc("studentSectionAssociation")
                .exposeAs(ResourceNames.STUDENT_SECTION_ASSOCIATIONS).storeAs("studentSectionAssociation")
                .from(student, "getStudent", "getStudents").to(section, "getSection", "getSections")
                .calledFromSource("getStudentSectionAssociations").calledFromTarget("getStudentSectionAssociations")
                .build();
        addDefinition(studentSectionAssociation);
        
        AssociationDefinition teacherSchoolAssociation = factory.makeAssoc("teacherSchoolAssociation")
                .exposeAs(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS).storeAs("teacherSchoolAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(school, "getSchool", "getSchools")
                .calledFromSource("getTeacherSchoolAssociations").calledFromTarget("getTeacherSchoolAssociations")
                .build();
        addDefinition(teacherSchoolAssociation);
        
        AssociationDefinition staffEducationOrganizationAssociation = factory.makeAssoc("staffEducationOrganizationAssociation")
                .exposeAs(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)
                .storeAs("staffEducationOrganizationAssociation")
                .from(staff, "getStaff", "getStaff", "staffReference")
                .to(educationOrganization, "getEducationOrganization", "getEducationOrganizations", "educationOrganizationReference")
                .calledFromSource("getStaffEducationOrganizationAssociations")
                .calledFromTarget("getStaffEducationOrganizationAssociations").build();
        addDefinition(staffEducationOrganizationAssociation);
        
        AssociationDefinition sectionAssessmentAssociation = factory.makeAssoc("sectionAssessmentAssociation")
                .exposeAs(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS).storeAs("sectionAssessmentAssociation")
                .from(section, "getSection", "getSections").to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getSectionAssessmentAssociations")
                .calledFromTarget("getSectionAssessmentAssociations").build();
        addDefinition(sectionAssessmentAssociation);
        
        AssociationDefinition educationOrganizationAssociation = factory
                .makeAssoc("educationOrganizationAssociation")
                .exposeAs("educationOrganization-associations")
                .storeAs("educationOrganizationAssociation")
                .from(educationOrganization, "getEducationOrganizationParent", "getEducationOrganizationParents",
                        "educationOrganizationParentId")
                .to(educationOrganization, "getEducationOrganizationChild", "getEducationOrganizationChilds",
                        "educationOrganizationChildId").calledFromSource("getEducationOrganizationAssociations")
                .calledFromTarget("getEducationOrganizationAssociations").build();
        addDefinition(educationOrganizationAssociation);
        
        AssociationDefinition schoolSessionAssociation = factory.makeAssoc("schoolSessionAssociation")
                .exposeAs(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS).storeAs("schoolSessionAssociation")
                .from(school, "getSchool", "getSchools").to(session, "getSession", "getSessions")
                .calledFromSource("getSchoolSessionAssociations").calledFromTarget("getSchoolSessionAssociations")
                .build();
        addDefinition(schoolSessionAssociation);

        AssociationDefinition sessionCourseAssociation = factory.makeAssoc("sessionCourseAssociation")
                .exposeAs(ResourceNames.SESSION_COURSE_ASSOCIATIONS).storeAs("sessionCourseAssociation")
                .from(session, "getSession", "getSessions").to(course, "getCourse", "getCourses")
                .calledFromSource("getSessionCourseAssociations").calledFromTarget("getSessionCourseAssociations")
                .build();
        addDefinition(sessionCourseAssociation);

        AssociationDefinition studentTranscriptAssociation = factory.makeAssoc("studentTranscriptAssociation")
                .exposeAs(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS).storeAs("studentTranscriptAssociation")
                .from(student, "getStudent", "getStudents").to(course, "getCourse", "getCourses")
                .calledFromSource("getStudentTranscriptAssociations").calledFromTarget("getStudentTranscriptAssociations")
                .build();
        addDefinition(studentTranscriptAssociation);
        
        // Adding the security collection
        EntityDefinition roles = factory.makeEntity("roles").storeAs("roles").build();
        addDefinition(roles);
        addDefinition(factory.makeEntity("realm").storeAs("realm").build());
        addDefinition(factory.makeEntity("authSession").build());
        
        // Adding the application collection
        addDefinition(factory.makeEntity("application").storeAs("application").build());
        
        // Adding OAuth 2.0 Services
        addDefinition(factory.makeEntity("oauthAuthorizationCode").build());
        addDefinition(factory.makeEntity("oauth_access_token").build());
        addDefinition(factory.makeEntity("oauth_refresh_token").build());
        
        this.registerDirectReferences();
    }
    
    /**
     * done last to avoid existence issues
     * 
     */
    private void registerDirectReferences() {

        //
        LOG.debug("Registering direct entity references");
        
        int referencesLoaded = 0;
        
        //loop for each entity that is defined
        for (EntityDefinition referringDefinition : this.mapping.values()) {
            //loop for each reference field on the entity
            for (Entry<String, ReferenceSchema> fieldSchema : referringDefinition.getReferenceFields().entrySet()) {
                ReferenceSchema schema = fieldSchema.getValue(); //access to the reference schema
                String resource  = ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.get(schema.getResourceName());
                EntityDefinition referencedEntity = this.mapping.get(resource);
                
                LOG.debug("* New reference: " + referringDefinition.getStoredCollectionName() + "." + fieldSchema.getKey() 
                        + " -> " + schema.getResourceName() + "._id");
                
                //tell the referenced entity that some entity definition refers to it
                referencedEntity.addReferencingEntity(referringDefinition);
                referencesLoaded++;
            }
        }
        
        //print stats
        LOG.debug("" + referencesLoaded + " direct references loaded.");
    }
    
    public void addDefinition(EntityDefinition defn) {
        LOG.debug("adding definition for {}", defn.getResourceName());
        defn.setSchema(repo.getSchema(defn.getStoredCollectionName()));
        ResourceNames.ENTITY_RESOURCE_NAME_MAPPING.put(defn.getType(), defn.getResourceName());
        this.mapping.put(defn.getResourceName(), defn);
    }
}
