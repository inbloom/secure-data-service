package org.slc.sli.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.slc.sli.client.SliClient;
import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.GradeLevelType;

@Controller
@RequestMapping("studentschoolassociationmenu.html")
public class StudentSchoolAssociationController {
    
    private final Logger log = LoggerFactory.getLogger(StudentSchoolAssociationController.class);
    
    @Autowired
    private SliClient client;
    
    @RequestMapping(method = RequestMethod.POST)
    public String handleAction(@RequestParam(value = "action") String action,
            @ModelAttribute StudentSchoolAssociation ssa, ModelMap model) {
        
        Integer studentId = ssa.getStudentId();
        Integer schoolId = ssa.getSchoolId();
        
        log.debug(
                "Action Requested: {} associationId: {} schoolId: {} studentId: {} entryGradeLevel: {}",
                new Object[] { action, ssa.getAssociationId(), ssa.getSchoolId(), ssa.getStudentId(),
                        ssa.getEntryGradeLevel() });
        
        if (action == null) {
            action = "";
        }
        
        Student student = new Student();
        student.setStudentId(studentId);
        School school = new School();
        school.setSchoolId(schoolId);
        
        if (action.equalsIgnoreCase("add")) {
            log.debug("Adding association: {}", ssa.getAssociationId());
            ssa.setEntryDate(Calendar.getInstance());
            boolean worked = client.associate(school, student, ssa) != null;
            if (!worked) {
                log.error("Add action failed.");
            }
            
        } else if (action.equalsIgnoreCase("update")) {
            log.debug("Updating association: {}", ssa.getAssociationId());
            ssa.setEntryDate(Calendar.getInstance());
            boolean worked = client.reassociate(school, student, ssa);
            if (!worked) {
                log.error("Update action failed.");
            }
            
        } else if (action.equalsIgnoreCase("delete")) {
            boolean worked = client.disassociate(school, student, ssa);
            if (!worked) {
                log.error("Delete action failed.");
            }
        }
        
        String query = "";
        if (studentId != null && schoolId != null) {
            query = "?schoolId=" + schoolId + "&studentId=" + studentId;
        } else if (studentId != null) {
            query = "?studentId=" + studentId;
        } else if (schoolId != null) {
            query = "?schoolId=" + schoolId;
        }
        return "redirect:/spring/studentschoolassociationmenu.html" + query;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String listAssociations(@RequestParam(value = "studentId", required = false) Integer studentId,
            @RequestParam(value = "schoolId", required = false) Integer schoolId, Model model) {
        if (schoolId != null && schoolId == -1) {
            schoolId = null;
        }
        if (studentId != null && studentId == -1) {
            studentId = null;
        }
        
        model.addAttribute("filterStudentId", studentId);
        model.addAttribute("filterSchoolId", schoolId);
        
        List<StudentSchoolAssociation> ssas = new LinkedList<StudentSchoolAssociation>();
        Student student = new Student();
        student.setStudentId(studentId);
        School school = new School();
        school.setSchoolId(schoolId);
        if (studentId != null && schoolId != null) {
            for (StudentSchoolAssociation ssa : client.getAssociations(student, school, StudentSchoolAssociation.class)) {
                ssas.add(ssa);
            }
        } else if (studentId != null) {
            for (StudentSchoolAssociation ssa : client.getAssociated(student, StudentSchoolAssociation.class)) {
                ssas.add(ssa);
            }
        } else if (schoolId != null) {
            for (StudentSchoolAssociation ssa : client.getAssociated(school, StudentSchoolAssociation.class)) {
                ssas.add(ssa);
            }
        }
        
        model.addAttribute("associations", ssas);
        
        List<Student> students = new LinkedList<Student>();
        for (Student s : client.list(Student.class)) {
            students.add(s);
        }
        model.addAttribute("students", students);
        
        List<School> schools = new LinkedList<School>();
        for (School s : client.list(School.class)) {
            schools.add(s);
        }
        model.addAttribute("schools", schools);
        
        model.addAttribute("gradeLevels", GradeLevelType.class.getEnumConstants());
        
        return "studentSchoolAssociation";
    }
    
}
