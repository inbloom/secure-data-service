package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.repository.StudentRepository;

/**
 * TODO Need to figure out paging strategy - repository is paged, but we're
 * using the findAll (as opposed to findAll( Pageable ) )
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Service
public class StudentRepositoryService implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public Collection<Student> getAll() throws Exception {
        
        Iterable<Student> students = studentRepository.findAll();
        Iterator<Student> iter = students.iterator();
        
        List<Student> list = new ArrayList<Student>();
        
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        
        return list;
    }
    
    @Transactional
    public void deleteById(int id) throws Exception {
        Student student = studentRepository.findOne(id);
        if (student != null) {
            studentRepository.deleteWithAssoc(id);
        }
    }
    
    @Transactional
    public void addOrUpdate(Student student) throws Exception {
        studentRepository.save(student);
        
    }
    
    public Student getStudentById(int studentId) {
        return studentRepository.findOne(studentId);
    }
    
    @Override
    public Collection<School> getSchoolsByStudent(Student student) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
