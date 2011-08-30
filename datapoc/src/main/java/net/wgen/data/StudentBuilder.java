package net.wgen.data;

import javax.annotation.PostConstruct;

import net.wgen.domain.Student;
import net.wgen.service.StudentService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
@Component
public class StudentBuilder  {

	@Autowired StudentService studentService;
	
	@PostConstruct
	public void init() {
		
		Student sean = new Student();
		sean.setFirstName("Sean" );
		sean.setLastName("M");
		
		studentService.addStudent(sean);
		
	}
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		
	}

}
