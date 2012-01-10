package org.slc.sli.api.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.validation.EntityValidator;
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
    private Map<EntityDefinition, Collection<AssociationDefinition>> links = new HashMap<EntityDefinition, Collection<AssociationDefinition>>();
    
    @Autowired
    private EntityRepository defaultRepo;
    
    @Autowired
    private EntityValidator validator;
    
    @Override
    public EntityDefinition lookupByResourceName(String resourceName) {
        return mapping.get(resourceName);
    }
    
    @Override
    public Collection<AssociationDefinition> getLinked(EntityDefinition defn) {
        return links.get(defn);
    }
    
    @PostConstruct
    @Override
    public void init() {
        EntityDefinition.setDefaultRepo(defaultRepo);
        EntityDefinition.addGlobalTreatment(new IdTreatment());
        
        // adding the entity definitions
        EntityDefinition aggregation = this.makeExposeAndAddEntityDefinition("aggregation");
        EntityDefinition assessment = this.makeExposeAndAddEntityDefinition("assessment");
        EntityDefinition school = this.makeExposeAndAddEntityDefinition("school");
        EntityDefinition section = this.makeExposeAndAddEntityDefinition("section");
        EntityDefinition staff = this.makeExposeAndAddEntityDefinition("staff", "staff");
        EntityDefinition student = this.makeExposeAndAddEntityDefinition("student");
        EntityDefinition teacher = this.makeExposeAndAddEntityDefinition("teacher");
        EntityDefinition educationOrganization = this.makeExposeAndAddEntityDefinition("educationOrganization");
        
        // adding the association definitions
        AssociationDefinition studentSchoolAssociation = AssociationDefinition
                .makeAssoc("studentSchoolAssociation", validator).exposeAs("student-school-associations")
                .storeAs("studentschoolassociation").from(student, "getStudent", "getStudents")
                .to(school, "getSchool", "getSchools").calledFromSource("getStudentSchoolAssociations")
                .calledFromTarget("getStudentSchoolAssociations").build();
        addAssocDefinition(studentSchoolAssociation);
        
        AssociationDefinition teacherSectionAssociation = AssociationDefinition
                .makeAssoc("teacherSectionAssociation", validator).exposeAs("teacher-section-associations")
                .storeAs("teachersectionassociation").from(teacher, "getTeacher", "getTeachers")
                .to(section, "getSection", "getSections").calledFromSource("getTeacherSectionAssociations")
                .calledFromTarget("getTeacherSectionAssociations").build();
        addAssocDefinition(teacherSectionAssociation);

        AssociationDefinition studentAssessmentAssociation = AssociationDefinition
                .makeAssoc("studentAssessmentAssociation", validator).exposeAs("student-assessment-associations")
                .storeAs("studentassessmentassociation").from(student, "getStudent", "getStudents")
                .to(assessment, "getAssessment", "getAssessments")
                .calledFromSource("getStudentAssessmentAssociations")
                .calledFromTarget("getStudentAssessmentAssociations").build();
        addAssocDefinition(studentAssessmentAssociation);
        
        AssociationDefinition studentSectionAssociation = AssociationDefinition
                .makeAssoc("studentSectionAssociation", validator).exposeAs("student-section-associations")
                .storeAs("studentsectionassociation").from(student, "getStudent", "getStudents")
                .to(section, "getSection", "getSections").calledFromSource("getStudentSectionAssociations")
                .calledFromTarget("getStudentSectionAssociations").build();
        addAssocDefinition(studentSectionAssociation);
        
        AssociationDefinition teacherSchoolAssociation = AssociationDefinition
                .makeAssoc("teacherSchoolAssociation", validator).exposeAs("teacher-school-associations")
                .storeAs("teacherschoolassociation").from(teacher, "getTeacher", "getTeachers")
                .to(school, "getSchool", "getSchools").calledFromSource("getTeacherSchoolAssociations")
                .calledFromTarget("getTeacherSchoolAssociations").build();
        addAssocDefinition(teacherSchoolAssociation);
        
        AssociationDefinition educationOrganizationSchoolAssociation = AssociationDefinition
                .makeAssoc("educationOrganizationSchoolAssociation", validator)
                .exposeAs("educationOrganization-school-associations")
                .storeAs("educationOrganizationSchoolAssociation")
                .from(educationOrganization, "getEducationOrganization", "getSchoolsAssigned")
                .to(school, "getSchool", "getEducationOrganizationsAssigned")
                .calledFromSource("getSchoolsAssigned")
                .calledFromTarget("getEducationOrganizationsAssigned").build();
        addAssocDefinition(educationOrganizationSchoolAssociation);
        
        // Adding the security collection
        EntityDefinition roles = EntityDefinition.makeEntity("roles", validator).storeAs("roles").build();
        addDefinition(roles);
        addDefinition(EntityDefinition.makeEntity("realm", validator).build());
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
        //create the entity definition and expose it under the pluralized name ("teacher" exposed as "teachers")
        EntityDefinition definition = EntityDefinition.makeEntity(entityName, this.validator).exposeAs(exposeName).build();
        //add the new definition to the map of known definitions
        this.addDefinition(definition);
        //return newly created definition
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
