package org.slc.sli.api.service;

import org.springframework.stereotype.Service;

import org.slc.sli.domain.Student;

@Service
public class SecureFakeServiceImpl implements SecureFakeService {
    
    public void foo(Student student) {
        
        System.out.println("Student: " + student);
        
    }
    
}
