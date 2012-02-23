package org.slc.sli.api.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.SchemaRepository;

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
    private Map<String, String> entityResourceNameMapping = new HashMap<String, String>();
    private Map<EntityDefinition, Collection<AssociationDefinition>> links = new HashMap<EntityDefinition, Collection<AssociationDefinition>>();
    
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
        return mapping.get(entityResourceNameMapping.get(entityType));
    }
    
    @Override
    public Collection<AssociationDefinition> getLinked(EntityDefinition defn) {
        return links.get(defn);
    }
    
    @PostConstruct
    @Autowired
    public void init() {
        
        // adding the entity definitions
        factory.makeEntity("aggregation", "aggregations").buildAndRegister(this);
        factory.makeEntity("aggregationDefinition", "aggregationDefinitions").buildAndRegister(this);
        EntityDefinition assessment = factory.makeEntity("assessment", "assessments").buildAndRegister(this);
        EntityDefinition course = factory.makeEntity("course", "courses").buildAndRegister(this);
        EntityDefinition school = factory.makeEntity("school", "schools").buildAndRegister(this);
        EntityDefinition section = factory.makeEntity("section", "sections").buildAndRegister(this);
        EntityDefinition session = factory.makeEntity("session", "sessions").buildAndRegister(this);
        EntityDefinition staff = factory.makeEntity("staff").buildAndRegister(this);
        EntityDefinition student = factory.makeEntity("student", "students").buildAndRegister(this);
        EntityDefinition teacher = factory.makeEntity("teacher", "teachers").buildAndRegister(this);
        EntityDefinition educationOrganization = factory.makeEntity("educationOrganization", "educationOrganizations")
                .buildAndRegister(this);
        
        // adding the association definitions
        AssociationDefinition studentSchoolAssociation = factory.makeAssoc("studentSchoolAssociation")
                .exposeAs("student-school-associations").storeAs("studentSchoolAssociation")
                .from(student, "getStudent", "getStudents").to(school, "getSchool", "getSchools")
                .calledFromSource("getStudentSchoolAssociations").calledFromTarget("getStudentSchoolAssociations")
                .build();
        addAssocDefinition(studentSchoolAssociation);
        
        AssociationDefinition teacherSectionAssociation = factory.makeAssoc("teacherSectionAssociation")
                .exposeAs("teacher-section-associations").storeAs("teacherSectionAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(section, "getSection", "getSections")
                .calledFromSource("getTeacherSectionAssociations").calledFromTarget("getTeacherSectionAssociations")
                .build();
        addAssocDefinition(teacherSectionAssociation);
        
        AssociationDefinition studentAssessmentAssociation = factory.makeAssoc("studentAssessmentAssociation")
                .exposeAs("student-assessment-associations").storeAs("studentAssessmentAssociation")
                .from(student, "getStudent", "getStudents").to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getStudentAssessmentAssociations")
                .calledFromTarget("getStudentAssessmentAssociations").build();
        addAssocDefinition(studentAssessmentAssociation);
        
        AssociationDefinition studentSectionAssociation = factory.makeAssoc("studentSectionAssociation")
                .exposeAs("student-section-associations").storeAs("studentSectionAssociation")
                .from(student, "getStudent", "getStudents").to(section, "getSection", "getSections")
                .calledFromSource("getStudentSectionAssociations").calledFromTarget("getStudentSectionAssociations")
                .build();
        addAssocDefinition(studentSectionAssociation);
        
        AssociationDefinition teacherSchoolAssociation = factory.makeAssoc("teacherSchoolAssociation")
                .exposeAs("teacher-school-associations").storeAs("teacherSchoolAssociation")
                .from(teacher, "getTeacher", "getTeachers").to(school, "getSchool", "getSchools")
                .calledFromSource("getTeacherSchoolAssociations").calledFromTarget("getTeacherSchoolAssociations")
                .build();
        addAssocDefinition(teacherSchoolAssociation);
        
        AssociationDefinition educationOrganizationSchoolAssociation = factory
                .makeAssoc("educationOrganizationSchoolAssociation")
                .exposeAs("educationOrganization-school-associations")
                .storeAs("educationOrganizationSchoolAssociation")
                .from(educationOrganization, "getEducationOrganization", "getEducationOrganizations")
                .to(school, "getSchool", "getSchools").calledFromSource("getSchoolsAssigned")
                .calledFromTarget("getEducationOrganizationsAssigned").build();
        addAssocDefinition(educationOrganizationSchoolAssociation);
        
        AssociationDefinition staffEducationOrganizationAssociation = factory
                .makeAssoc("staffEducationOrganizationAssociation")
                .exposeAs("staff-educationOrganization-associations").storeAs("staffEducationOrganizationAssociation")
                .from(staff, "getStaff", "getStaff")
                .to(educationOrganization, "getEducationOrganization", "getEducationOrganizations")
                .calledFromSource("getEducationOrganizationsAssigned").calledFromTarget("getStaffAssigned").build();
        addAssocDefinition(staffEducationOrganizationAssociation);
        
        AssociationDefinition sectionAssessmentAssociation = factory.makeAssoc("sectionAssessmentAssociation")
                .exposeAs("section-assessment-associations").storeAs("sectionAssessmentAssociation")
                .from(section, "getSection", "getSections").to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getSectionAssessmentAssociations")
                .calledFromTarget("getSectionAssessmentAssociations").build();
        addAssocDefinition(sectionAssessmentAssociation);
        
        
        AssociationDefinition educationOrganizationAssociation = factory
                .makeAssoc("educationOrganizationAssociation")
                .exposeAs("educationOrganization-associations")
                .storeAs("educationOrganizationAssociation")
                .from(educationOrganization, "getEducationOrganizationParent", "getEducationOrganizationParents",
                        "educationOrganizationParentId")
                .to(educationOrganization, "getEducationOrganizationChild", "getEducationOrganizationChilds",
                        "educationOrganizationChildId").calledFromSource("getEducationOrganizationAssociations")
                .calledFromTarget("getEducationOrganizationAssociations").build();
        addAssocDefinition(educationOrganizationAssociation);
        
        AssociationDefinition schoolSessionAssociation = factory.makeAssoc("schoolSessionAssociation")
                .exposeAs("school-session-associations").storeAs("schoolSessionAssociation")
                .from(school, "getSchool", "getSchools").to(session, "getSession", "getSessions")
                .calledFromSource("getSchoolSessionAssociations").calledFromTarget("getSchoolSessionAssociations")
                .build();
        addAssocDefinition(schoolSessionAssociation);
        
        AssociationDefinition sessionCourseAssociation = factory.makeAssoc("sessionCourseAssociation")
                .exposeAs("session-course-associations").storeAs("sessionCourseAssociation")
                .from(session, "getSession", "getSessions").to(course, "getCourse", "getCourses")
                .calledFromSource("getSessionCourseAssociations").calledFromTarget("getSessionCourseAssociations")
                .build();
        addAssocDefinition(sessionCourseAssociation);
        
        // TODO: Known technical-debt to be replaced by internal reference
        // fields (such as
        // section.courseId)
        AssociationDefinition courseSectionAssociation = factory.makeAssoc("courseSectionAssociation")
                .exposeAs("course-section-associations").storeAs("courseSectionAssociation")
                .from(course, "getCourse", "getCourses").to(section, "getSection", "getSections")
                .calledFromSource("getCourseSectionAssociations").calledFromTarget("getCourseSectionAssociations")
                .build();
        addAssocDefinition(courseSectionAssociation);
        
        // Adding the security collection
        EntityDefinition roles = factory.makeEntity("roles").storeAs("roles").build();
        addDefinition(roles);
        addDefinition(factory.makeEntity("realm").storeAs("realm").build());
        addDefinition(factory.makeEntity("authSession").build());
        
        // Adding the application collection
        addDefinition(factory.makeEntity("application").storeAs("application").build());
        
        // Adding the OAuth 2.0 Authorized Session Manager (uses Token Manager)
        addDefinition(factory.makeEntity("oauthSession").storeAs("oauthSession").build());
    }
    
    private void add(EntityDefinition defn) {
        defn.setSchema(repo.getSchema(defn.getStoredCollectionName()));
        entityResourceNameMapping.put(defn.getType(), defn.getResourceName());
        this.mapping.put(defn.getResourceName(), defn);
    }
    
    public void addDefinition(EntityDefinition defn) {
        LOG.debug("adding definition for {}", defn.getResourceName());
        add(defn);
        links.put(defn, new LinkedHashSet<AssociationDefinition>());
    }
    
    public void addAssocDefinition(AssociationDefinition defn) {
        LOG.debug("adding assoc for {}", defn.getResourceName());
        add(defn);
        EntityDefinition sourceEntity = defn.getSourceEntity();
        EntityDefinition targetEntity = defn.getTargetEntity();
        links.get(sourceEntity).add(defn);
        links.get(targetEntity).add(defn);
        mapping.get(sourceEntity.getResourceName()).addLinkedAssoc(defn);
        mapping.get(targetEntity.getResourceName()).addLinkedAssoc(defn);
    }
}
