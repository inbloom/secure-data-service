package org.slc.sli.api.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests are SecureFakeService
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml", "/spring/securityContext.xml" })
public class StudentServiceSecurityTest {
    
    @Autowired
    private StudentService service;
    private Student student;
    
    @Autowired
    @Qualifier("authenticationManager")
    private ProviderManager authenticationManager;
    
    @Autowired
    private TestingAuthenticationProvider testingAuthenticationProvider;
    
    @Before
    public void init() {
        
        authenticationManager.getProviders().add(testingAuthenticationProvider);
        
        student = StudentBuilder.buildTestStudent();
        
        SecurityContext context = SecurityContextHolder.getContext();
        
        Authentication authentication = new TestingAuthenticationToken(student, "credentials");
        context.setAuthentication(authentication);
    }
    
    public void testSecure() throws Exception {
        service.addOrUpdate(student);
        Student student2 = service.getStudentById(student.getStudentId());
        assertNotNull(student2);
    }
    
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void testNoAuthentication() {
        
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        try {
            service.addOrUpdate(student);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        Student student2 = service.getStudentById(student.getStudentId());
        assertNotNull(student2);
    }
    
}
