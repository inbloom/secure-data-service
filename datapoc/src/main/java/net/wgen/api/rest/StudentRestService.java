package net.wgen.api.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.wgen.domain.Student;
import net.wgen.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Produces ("application/json")
@Service("studentRestService")
public class StudentRestService {

	@Autowired
	private StudentService studentService;
	
	@GET 
	@Path("/students")
	public List<Student> getStudents() {
		
		return studentService.findAll();
	}
	
	@GET
	@Path("/student/{id}")
	public Student getStudent( @PathParam("id" ) Long id ) {
		
		return studentService.findById( id );
	
				
	}
	
	
}
