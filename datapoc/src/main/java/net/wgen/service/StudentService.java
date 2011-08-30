package net.wgen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.wgen.domain.Student;
import net.wgen.repo.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

	@Autowired StudentRepository studentRepository;
	
	public void addStudent( Student student ) {
		student.setSystemId( UUID.randomUUID().toString() );
		studentRepository.save( student );
	}

	public List<Student> findAll() {
		List<Student> students = new ArrayList();
		
		for ( Student s : studentRepository.findAll() ) {
			students.add( s );
		}
		return students;
	}

	public Student findById(Long id) {
		
		return studentRepository.findOne(id);
	}
	
	
}
