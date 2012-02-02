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

/**
 * Default implementation of the entity definition store
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
        this.makeExposeAndAddEntityDefinition("aggregation");
        this.makeExposeAndAddEntityDefinition("aggregationDefinition");
        EntityDefinition assessment = this.makeExposeAndAddEntityDefinition("assessment");
        EntityDefinition course = this.makeExposeAndAddEntityDefinition("course");
        EntityDefinition school = this.makeExposeAndAddEntityDefinition("school");
        EntityDefinition section = this.makeExposeAndAddEntityDefinition("section");
        EntityDefinition session = this.makeExposeAndAddEntityDefinition("session");
        EntityDefinition staff = this.makeExposeAndAddEntityDefinition("staff", "staff");
        EntityDefinition student = this.makeExposeAndAddEntityDefinition("student");
        EntityDefinition teacher = this.makeExposeAndAddEntityDefinition("teacher");
        EntityDefinition educationOrganization = this.makeExposeAndAddEntityDefinition("educationOrganization");
        
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
        
        AssociationDefinition sectionSchoolAssociation = factory.makeAssoc("sectionSchoolAssociation")
                .exposeAs("section-school-associations").storeAs("sectionSchoolAssociation")
                .from(section, "getSection", "getSections").to(school, "getSchool", "getSchools")
                .calledFromSource("getSectionSchoolAssociations").calledFromTarget("getSectionSchoolAssociations")
                .build();
        addAssocDefinition(sectionSchoolAssociation);

        AssociationDefinition educationOrganizationAssociation = factory.makeAssoc("educationOrganizationAssociation")
                .exposeAs("educationOrganization-associations").storeAs("educationOrganizationAssociation")
                .from(educationOrganization, "getEducationOrganizationParent", "getEducationOrganizationParents", "educationOrganizationParentId")
                .to(educationOrganization, "getEducationOrganizationChild", "getEducationOrganizationChilds", "educationOrganizationChildId")
                .calledFromSource("getEducationOrganizationAssociations")
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

        // Adding the security collection
        EntityDefinition roles = factory.makeEntity("roles").storeAs("roles").build();
        addDefinition(roles);
        addDefinition(factory.makeEntity("realm").storeAs("realm").build());
    }
    
    /**
     * Creates an entity definition for the supplied entity name, exposes it to the API,
     * adds it to the list of known entities, and then returns it to the method caller.
     * 
     * @param entityName
     *            name of entity (collection)
     * @return newly created entity definition for the supplied entity name
     */
    private EntityDefinition makeExposeAndAddEntityDefinition(String entityName) {
        return this.makeExposeAndAddEntityDefinition(entityName, entityName + "s");
    }
    
    /**
     * Creates an entity definition for the supplied entity name, exposes it to the API,
     * adds it to the list of known entities, and then returns it to the method caller.
     * 
     * @param entityName
     *            name of entity (collection)
     * @param exposeName
     *            text in URI where entity will be exposed
     * @return newly created entity definition for the supplied entity name
     */
    private EntityDefinition makeExposeAndAddEntityDefinition(String entityName, String exposeName) {
        // create the entity definition and expose it under the pluralized name ("teacher" exposed
        // as "teachers")
        EntityDefinition definition = factory.makeEntity(entityName).exposeAs(exposeName).build();
        entityResourceNameMapping.put(entityName, exposeName);
        
        // add and return the newly created definition
        this.addDefinition(definition);
        return definition;
    }
    
    private void add(EntityDefinition defn) {
        this.mapping.put(defn.getResourceName(), defn);
    }
    
    private void addDefinition(EntityDefinition defn) {
        LOG.debug("adding definition for {}", defn.getResourceName());
        add(defn);
        links.put(defn, new LinkedHashSet<AssociationDefinition>());
    }
    
    private void addAssocDefinition(AssociationDefinition defn) {
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
