package org.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.repository.SchoolRepository;
import org.slc.sli.repository.StudentRepository;
import org.slc.sli.repository.StudentSchoolAssociationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sli.ingestion.StudentSchoolAssociationInterchange;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;


@Component
public class ContextManager implements ApplicationContextAware {

	Logger log = LoggerFactory.getLogger( ContextManager.class );
	
	public static final String SLI_REPOSITORY_PACKAGE = "org.slc.sli.repository";
	public static final String SLI_REPOSITORY_SUFFIX = "Repository";
	public static final String SLI_INTERCHANGE_SUFFIX = "Interchange";
	
	// Spring ApplicationContext
	private ApplicationContext applicationContext = null;

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private StudentSchoolAssociationRepository studentSchoolAssociationRepository;
	
	private Map classesMap = new HashMap();
	private Map repositoryMap = new HashMap();
	
	public ContextManager() {
	}
	
	@PostConstruct
	public void init() {
		this.classesMap.put(Student.class.getName(), Student.class);
		this.classesMap.put(School.class.getName(), School.class);
		this.classesMap.put(StudentSchoolAssociation.class.getName(), StudentSchoolAssociation.class);
		
		this.repositoryMap.put(Student.class.getName(), studentRepository);
		this.repositoryMap.put(School.class.getName(), schoolRepository);
		this.repositoryMap.put(StudentSchoolAssociation.class.getName(), studentSchoolAssociationRepository);
		this.repositoryMap.put(StudentSchoolAssociationInterchange.class.getName(), studentSchoolAssociationRepository);
	}
	
	// ApplicationContextAware contract
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
    public List getClassNames() {
		return new ArrayList(this.classesMap.keySet());
	}
	
	public PagingAndSortingRepository getRepository(String domainClassName) {
		return (PagingAndSortingRepository)this.repositoryMap.get(domainClassName);
	}
	
	public PagingAndSortingRepository getRepositoryByConvention(String domainClassName) {
		PagingAndSortingRepository repository = null;
		
		// Derive Repository class name using convention
		if (domainClassName.endsWith(SLI_INTERCHANGE_SUFFIX)) {
			domainClassName = domainClassName.substring(0, (domainClassName.length() - SLI_INTERCHANGE_SUFFIX.length()));
		}
		int domainClassIndex = domainClassName.lastIndexOf('.');
		String repositoryClassName = domainClassName.substring(domainClassIndex);
		repositoryClassName = SLI_REPOSITORY_PACKAGE + repositoryClassName + SLI_REPOSITORY_SUFFIX;
		
		try {
        	Class repositoryClass = Class.forName(repositoryClassName);
        	
        	// Lookup Repository bean in Spring ApplicationContext
    		repository = (PagingAndSortingRepository)getApplicationContext().getBean(repositoryClass);
    	} 
    	catch (ClassNotFoundException classNotFoundException) {
    		log.error("invalid ingestion repository class type: " + repositoryClassName);
    	}
		
		return repository;
	}
	
}
