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

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.validation.EntityValidator;

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
        EntityDefinition section = EntityDefinition.makeEntity("section", validator).exposeAs("sections").build();
        addDefinition(section);
        EntityDefinition student = EntityDefinition.makeEntity("student", validator).exposeAs("students").build();
        addDefinition(student);
        EntityDefinition school = EntityDefinition.makeEntity("school", validator).exposeAs("schools").build();
        addDefinition(school);
        EntityDefinition assessment = EntityDefinition.makeEntity("assessment", validator).exposeAs("assessments")
                .build();
        addDefinition(assessment);
        
        AssociationDefinition studentSchoolAssociation = AssociationDefinition
                .makeAssoc("studentSchoolAssociation", validator).exposeAs("student-school-associations")
                .storeAs("studentschoolassociation").from(student, "getStudent", "getStudentsEnrolled")
                .to(school, "getSchool", "getSchoolsAttended").calledFromSource("getStudentEnrollments")
                .calledFromTarget("getSchoolEnrollments").build();
        addAssocDefinition(studentSchoolAssociation);
        
        EntityDefinition teacher = EntityDefinition.makeEntity("teacher", validator).exposeAs("teachers").build();
        addDefinition(teacher);
        
        AssociationDefinition teacherSectionAssociation = AssociationDefinition.makeAssoc("teacherSectionAssociation")
                .exposeAs("teacher-section-associations").storeAs("teachersectionassociation")
                .from(teacher, "getTeacher", "getSectionsAssigned").to(section, "getSection", "getTeachersAssigned")
                .calledFromSource("getSectionsAssigned").calledFromTarget("getTeachersAssigned").build();
        addAssocDefinition(teacherSectionAssociation);

        AssociationDefinition studentAssessmentAssociation = AssociationDefinition
                .makeAssoc("studentAssessmentAssociation", validator).exposeAs("student-assessment-associations")
                .storeAs("studentassessmentassociation").from(student, "getStudent", "getAssessmentsAssigned")
                .to(assessment, "getAssessment", "getStudentsAssigned")
                .calledFromSource("getStudentAssessmentAssociations")
                .calledFromTarget("getStudentAssessmentAssociations").build();
        addAssocDefinition(studentAssessmentAssociation);
        
        AssociationDefinition studentSectionAssociation = AssociationDefinition
                .makeAssoc("studentSectionAssociation", validator).exposeAs("student-section-associations")
                .storeAs("studentsectionassociation").from(student, "getStudent", "getSectionsAssigned")
                .to(section, "getSection", "getStudentsAssigned").calledFromSource("getSectionsAssigned")
                .calledFromTarget("getStudentsAssigned").build();
        addAssocDefinition(studentSectionAssociation);
        
        AssociationDefinition teacherSchoolAssociation = AssociationDefinition
                .makeAssoc("teacherSchoolAssociation").exposeAs("teacher-school-associations")
                .storeAs("teacherschoolassociation").from(teacher, "getTeacher", "getSchoolsAssigned")
                .to(school, "getSchool", "getTeachersAssigned").calledFromSource("getSchoolsAssigned")
                .calledFromTarget("getTeachersAssigned").build();
        addAssocDefinition(teacherSchoolAssociation);
        
        // Adding the security collection
        EntityDefinition roles = EntityDefinition.makeEntity("roles", validator).storeAs("roles").build();
        addDefinition(roles);
        addDefinition(EntityDefinition.makeEntity("realm", validator).build());
    }
    
    private void add(EntityDefinition defn) {
        mapping.put(defn.getResourceName(), defn);
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
