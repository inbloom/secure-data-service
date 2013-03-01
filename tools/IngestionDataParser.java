package org.slc.sli.data.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

public class IngestionDataParser {
    
    private static long update(Map<String, Long> counts, Map<String, List<Pair<String, String>>> interchanges, 
            String directory, PrintStream log, boolean verbose) {
        long interchangeRecordCount = 0;
        try {
            
            for (Map.Entry<String, List<Pair<String, String>>> entry : interchanges.entrySet()) {
                String interchange = directory + entry.getKey();
                FileReader in = new FileReader(interchange);
                BufferedReader reader = new BufferedReader(in);
                
                if (verbose) {
                    log.println(" [file] Reading from file: " + interchange);
                }
                
                List<Pair<String, String>> entities = entry.getValue();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    for (int i = 0; i < entities.size(); i++) {
                        Pair<String, String> pair = entities.get(i);
                        Pattern p = Pattern.compile(pair.getRight());
                        Matcher m = p.matcher(line);
                        if(m.find()) {
                            if (counts.containsKey(pair.getLeft())) {
                                long current = counts.get(pair.getLeft());
                                counts.put(pair.getLeft(), current + 1);
                                interchangeRecordCount++;
                            } else {
                                counts.put(pair.getLeft(), 1L);
                            }
                        }
                    }
                }
                
                if (verbose) {
                    log.println(" [file] Closing file handle: " + interchange);
                }
                
                reader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interchangeRecordCount;
    }
    
    /**
     * Returns the total number of records to be persisted across all interchanges.
     * 
     * @param directory directory where to find interchanges.
     * @return integer representing summation of all persist-able records in each interchange.
     */
    public static Pair<Long, Map<String, Long>> count(String directory, PrintStream log, boolean verbose) {        
        Map<String, Long> counts = new HashMap<String, Long>();
        Map<String, List<Pair<String, String>>> interchanges = new HashMap<String, List<Pair<String, String>>>();
        
        List<Pair<String, String>> assessmentMetadataPairs = new ArrayList<Pair<String, String>>();
        assessmentMetadataPairs.add(Pair.of("LearningStandard", "<LearningStandard id"));
        assessmentMetadataPairs.add(Pair.of("LearningObjective", "<LearningObjective>"));
        assessmentMetadataPairs.add(Pair.of("AssessmentItem", "<AssessmentItem>"));
        assessmentMetadataPairs.add(Pair.of("PerformanceLevelDescriptor", "<PerformanceLevelDescriptor>"));
        assessmentMetadataPairs.add(Pair.of("ObjectiveAssessment", "<ObjectiveAssessment id"));
        assessmentMetadataPairs.add(Pair.of("AssessmentPeriodDescriptor", "<AssessmentPeriodDescriptor>"));
        assessmentMetadataPairs.add(Pair.of("AssessmentFamily", "<AssessmentFamily>"));
        assessmentMetadataPairs.add(Pair.of("Assessment", "<Assessment>"));
        
        List<Pair<String, String>> educationOrganizationPairs = new ArrayList<Pair<String, String>>();
        educationOrganizationPairs.add(Pair.of("StateEducationAgency", "<StateEducationAgency id"));
        educationOrganizationPairs.add(Pair.of("LocalEducationAgency", "<LocalEducationAgency id"));
        educationOrganizationPairs.add(Pair.of("School", "<School id"));
        educationOrganizationPairs.add(Pair.of("Course", "<Course id"));
        educationOrganizationPairs.add(Pair.of("Program", "<Program id"));
        
        List<Pair<String, String>> educationOrganizationCalendarPairs = new ArrayList<Pair<String, String>>();
        educationOrganizationCalendarPairs.add(Pair.of("Session", "<Session>"));
        educationOrganizationCalendarPairs.add(Pair.of("GradingPeriod", "<GradingPeriod id"));
        educationOrganizationCalendarPairs.add(Pair.of("CalendarDate", "<CalendarDate id"));
        
        List<Pair<String, String>> masterSchedulePairs = new ArrayList<Pair<String, String>>();
        masterSchedulePairs.add(Pair.of("CourseOffering", "<CourseOffering>"));
        masterSchedulePairs.add(Pair.of("Section", "<Section>"));        
        
        List<Pair<String, String>> staffAssociationPairs = new ArrayList<Pair<String, String>>();
        staffAssociationPairs.add(Pair.of("Teacher", "<Teacher id"));
        staffAssociationPairs.add(Pair.of("Staff", "<Staff id"));
        staffAssociationPairs.add(Pair.of("TeacherSchoolAssociation", "<TeacherSchoolAssociation>"));
        staffAssociationPairs.add(Pair.of("TeacherSectionAssociation", "<TeacherSectionAssociation>"));
        staffAssociationPairs.add(Pair.of("StaffEducationOrgAssignmentAssociation", "<StaffEducationOrgAssignmentAssociation>"));
        staffAssociationPairs.add(Pair.of("StaffProgramAssociation", "<StaffProgramAssociation>"));
        
        List<Pair<String, String>> studentAssessmentPairs = new ArrayList<Pair<String, String>>();
        studentAssessmentPairs.add(Pair.of("StudentAssessment", "<StudentAssessment id"));
        studentAssessmentPairs.add(Pair.of("StudentObjectiveAssessment", "<StudentObjectiveAssessment>"));
        studentAssessmentPairs.add(Pair.of("StudentAssessmentItem", "<StudentAssessmentItem id"));
        
        List<Pair<String, String>> studentAttendancePairs = new ArrayList<Pair<String, String>>();
        studentAttendancePairs.add(Pair.of("AttendanceEvent", "<AttendanceEvent>"));
        
        List<Pair<String, String>> studentCohortPairs = new ArrayList<Pair<String, String>>();
        studentCohortPairs.add(Pair.of("Cohort", "<Cohort>"));
        studentCohortPairs.add(Pair.of("StaffCohortAssociation", "<StaffCohortAssociation>"));
        studentCohortPairs.add(Pair.of("StudentCohortAssociation", "<StudentCohortAssociation>"));
        
        List<Pair<String, String>> studentDisciplinePairs = new ArrayList<Pair<String, String>>();
        studentDisciplinePairs.add(Pair.of("DisciplineIncident", "<DisciplineIncident id"));
        studentDisciplinePairs.add(Pair.of("StudentDisciplineIncidentAssociation", "<StudentDisciplineIncidentAssociation>"));
        studentDisciplinePairs.add(Pair.of("DisciplineAction", "<DisciplineAction>"));
        
        List<Pair<String, String>> studentEnrollmentPairs = new ArrayList<Pair<String, String>>();
        studentEnrollmentPairs.add(Pair.of("StudentSchoolAssociation", "<StudentSchoolAssociation>"));
        studentEnrollmentPairs.add(Pair.of("StudentSectionAssociation", "<StudentSectionAssociation>"));
        studentEnrollmentPairs.add(Pair.of("GraduationPlan", "<GraduationPlan id"));

        List<Pair<String, String>> studentGradePairs = new ArrayList<Pair<String, String>>();
        studentGradePairs.add(Pair.of("StudentAcademicRecord", "<StudentAcademicRecord id"));
        studentGradePairs.add(Pair.of("CourseTranscript", "<CourseTranscript id"));
        studentGradePairs.add(Pair.of("Grade", "<Grade id"));
        studentGradePairs.add(Pair.of("ReportCard", "<ReportCard id"));
        studentGradePairs.add(Pair.of("StudentCompetency", "<StudentCompetency id"));
        studentGradePairs.add(Pair.of("GradebookEntry", "<GradebookEntry id"));
        studentGradePairs.add(Pair.of("StudentGradebookEntry", "<StudentGradebookEntry>"));
        studentGradePairs.add(Pair.of("CompentencyLevelDescriptor", "<CompentencyLevelDescriptor id"));
        studentGradePairs.add(Pair.of("StudentCompetencyObjective", "<StudentCompetencyObjective id"));
        
        List<Pair<String, String>> studentParentPairs = new ArrayList<Pair<String, String>>();
        studentParentPairs.add(Pair.of("Student", "<Student id"));
        studentParentPairs.add(Pair.of("Parent", "<Parent id"));
        studentParentPairs.add(Pair.of("StudentParentAssociation", "<StudentParentAssociation>"));
        
        List<Pair<String, String>> studentProgramPairs = new ArrayList<Pair<String, String>>();
        studentProgramPairs.add(Pair.of("ServiceDescriptor", "<ServiceDescriptor>"));
        studentProgramPairs.add(Pair.of("StudentProgramAssociation", "<StudentProgramAssociation>"));
        
        interchanges.put("InterchangeAssessmentMetadata.xml", assessmentMetadataPairs);
        interchanges.put("InterchangeEducationOrganization.xml", educationOrganizationPairs);
        interchanges.put("InterchangeEducationOrgCalendar.xml", educationOrganizationCalendarPairs);
        interchanges.put("InterchangeMasterSchedule.xml", masterSchedulePairs);
        interchanges.put("InterchangeStaffAssociation.xml", staffAssociationPairs);
        interchanges.put("InterchangeStudentAssessment.xml", studentAssessmentPairs);
        interchanges.put("InterchangeStudentAttendance.xml", studentAttendancePairs);
        interchanges.put("InterchangeStudentCohort.xml", studentCohortPairs);
        interchanges.put("InterchangeStudentDiscipline.xml", studentDisciplinePairs);
        interchanges.put("InterchangeStudentEnrollment.xml", studentEnrollmentPairs);
        interchanges.put("InterchangeStudentGrade.xml", studentGradePairs);
        interchanges.put("InterchangeStudentParent.xml", studentParentPairs);
        interchanges.put("InterchangeStudentProgram.xml", studentProgramPairs);
        
        long total = update(counts, interchanges, directory, log, false);            

        if (verbose) {
            for (Map.Entry<String, Long> entry : counts.entrySet()) {
                String type = entry.getKey();
                long count = entry.getValue();
                log.println(" [info] entity: " + type + " --> count: " + count);
            }
        }                        
        return Pair.of(total, counts);
    }
    
    public static Map<String, Long> mapEntityCounts(String directory, Map<String, Long> entityCounts, PrintStream log, boolean verbose) {        
        Map<String, Long> counts = new HashMap<String, Long>();
        
        Map<String, List<String>> entityMappings = new HashMap<String, List<String>>();
        entityMappings.put("assessment", Arrays.asList("Assessment"));
        entityMappings.put("assessmentItem", Arrays.asList("AssessmentItem"));
        entityMappings.put("assessmentFamily", Arrays.asList("AssessmentFamily"));
        entityMappings.put("assessmentPeriodDescriptor", Arrays.asList("AssessmentPeriodDescriptor"));
        entityMappings.put("attendance", Arrays.asList("AttendanceEvent"));
        entityMappings.put("calendarDate", Arrays.asList("CalendarDate"));
        entityMappings.put("cohort", Arrays.asList("Cohort"));
        entityMappings.put("compentencyLevelDescriptor", Arrays.asList("CompentencyLevelDescriptor"));
        entityMappings.put("course", Arrays.asList("Course"));
        entityMappings.put("courseOffering", Arrays.asList("CourseOffering"));
        entityMappings.put("disciplineAction", Arrays.asList("DisciplineAction"));
        entityMappings.put("disciplineIncident", Arrays.asList("DisciplineIncident"));
        entityMappings.put("educationOrganization", Arrays.asList("StateEducationAgency", "LocalEducationAgency", "School"));
        entityMappings.put("grade", Arrays.asList("Grade"));
        entityMappings.put("gradebookEntry", Arrays.asList("GradebookEntry"));
        entityMappings.put("gradingPeriod", Arrays.asList("GradingPeriod"));
        entityMappings.put("graduationPlan", Arrays.asList("GraduationPlan"));
        entityMappings.put("learningObjective", Arrays.asList("LearningObjective"));
        entityMappings.put("learningStandard", Arrays.asList("LearningStandard"));
        entityMappings.put("objectiveAssessment", Arrays.asList("ObjectiveAssessment"));
        entityMappings.put("parent", Arrays.asList("Parent"));
        entityMappings.put("performanceLevelDescriptor", Arrays.asList("PerformanceLevelDescriptor"));
        entityMappings.put("program", Arrays.asList("Program"));
        entityMappings.put("reportCard", Arrays.asList("ReportCard"));
        entityMappings.put("schoolSessionAssociation", Arrays.asList("Session"));
        entityMappings.put("section", Arrays.asList("Section"));
        entityMappings.put("serviceDescriptor", Arrays.asList("ServiceDescriptor"));
        entityMappings.put("session", Arrays.asList("Session"));
        entityMappings.put("staff", Arrays.asList("Teacher", "Staff"));
        entityMappings.put("staffCohortAssociation", Arrays.asList("StaffCohortAssociation"));
        entityMappings.put("staffEducationOrgAssignmentAssociation", Arrays.asList("StaffEducationOrgAssignmentAssociation"));
        entityMappings.put("staffProgramAssociation", Arrays.asList("StaffProgramAssociation"));
        entityMappings.put("student", Arrays.asList("Student"));
        entityMappings.put("studentAcademicRecord", Arrays.asList("StudentAcademicRecord"));
        entityMappings.put("studentAssessmentAssociation", Arrays.asList("StudentAssessment"));
        entityMappings.put("studentAssessmentItem", Arrays.asList("StudentAssessmentItem"));
        entityMappings.put("studentCohortAssociation", Arrays.asList("StudentCohortAssociation"));
        entityMappings.put("studentCompetency", Arrays.asList("StudentCompetency"));
        entityMappings.put("studentCompetencyObjective", Arrays.asList("StudentCompetencyObjective"));
        entityMappings.put("studentDisciplineIncidentAssociation", Arrays.asList("StudentDisciplineIncidentAssociation"));
        entityMappings.put("studentGradebookEntry", Arrays.asList("StudentGradebookEntry"));
        entityMappings.put("studentObjectiveAssessment", Arrays.asList("StudentObjectiveAssessment"));
        entityMappings.put("studentParentAssociation", Arrays.asList("StudentParentAssociation"));
        entityMappings.put("studentProgramAssociation", Arrays.asList("StudentProgramAssociation"));
        entityMappings.put("studentSchoolAssociation", Arrays.asList("StudentSchoolAssociation"));
        entityMappings.put("studentSectionAssociation", Arrays.asList("StudentSectionAssociation"));
        entityMappings.put("studentTranscriptAssociation", Arrays.asList("CourseTranscript"));
        entityMappings.put("teacherSchoolAssociation", Arrays.asList("TeacherSchoolAssociation"));
        entityMappings.put("teacherSectionAssociation", Arrays.asList("TeacherSectionAssociation"));
        
        for (Map.Entry<String, List<String>> entry : entityMappings.entrySet()) {
            String collectionName = entry.getKey();
            List<String> accumulatedCollections = entry.getValue();
            long finalEntityCount = 0;
            
            for (int i = 0; i < accumulatedCollections.size(); i++) {
                if (entityCounts.containsKey(accumulatedCollections.get(i))) {
                    finalEntityCount += entityCounts.get(accumulatedCollections.get(i));
                }
            }
            counts.put(collectionName, finalEntityCount);
        }
        
        String studentEnrollmentFile = directory + "InterchangeStudentEnrollment.xml";
        counts.put("attendance", findUniqueStudentSchoolPairs(studentEnrollmentFile, log, verbose));
        
        return counts;
    }
    
    public static long findUniqueStudentSchoolPairs(String studentEnrollmentFile, PrintStream log, boolean verbose) {
        long uniquePairs = 0;
        try {
            List<Pair<String, String>> memory = new ArrayList<Pair<String, String>>();
            
            long repeaters = 0;
            FileReader in = new FileReader(studentEnrollmentFile);
            BufferedReader reader = new BufferedReader(in);
            
            if (verbose) {
                log.println(" [file] Reading from file: " + studentEnrollmentFile);
            }
            
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                Pattern p1 = Pattern.compile("<StudentSchoolAssociation>");
                Matcher m1 = p1.matcher(line);
                if(m1.find()) {
                    String studentUniqueStateId = null;
                    String stateOrganizationId = null;
                    for (Pattern p = Pattern.compile("</StudentSchoolAssociation>"); !p.matcher(line).find(); line = reader.readLine()) {
                        Pattern studentPattern = Pattern.compile("<StudentUniqueStateId>\\s*(.+)\\s*</StudentUniqueStateId>");
                        Matcher studentMatch = studentPattern.matcher(line);
                        if (studentMatch.find()) {
                            studentUniqueStateId = studentMatch.group(1);
                        }
                        
                        Pattern organizationPattern = Pattern.compile("<StateOrganizationId>\\s*(.+)\\s*</StateOrganizationId>");
                        Matcher organizationMatch = organizationPattern.matcher(line);
                        if (organizationMatch.find()) {
                            stateOrganizationId = organizationMatch.group(1);
                        }
                    }    
                    if (studentUniqueStateId != null && stateOrganizationId != null) {
                        if (!memory.contains(Pair.of(studentUniqueStateId, stateOrganizationId))) {
                            uniquePairs++;
                            memory.add(Pair.of(studentUniqueStateId, stateOrganizationId));
                        
                            if (verbose) {
                                log.println(" [info] Found student: " + studentUniqueStateId + " at school: " + stateOrganizationId);
                            }
                        } else {
                            if (verbose) {
                                log.println(" [warn] Already encountered student: " + studentUniqueStateId + " at school: " + stateOrganizationId);
                            }
                            repeaters++;
                        }
                    }
                }
            }
            
            if (verbose) {
                log.println(" [file] Closing file handle: " + studentEnrollmentFile);
                log.println(" [info] Found " + uniquePairs + " unique student-school associations.");
                log.println(" [info] Found " + repeaters + " repeated student-school associations.");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uniquePairs;
    }
    
    public static void write(String directory, Long total, Map<String, Long> counts, PrintStream log, boolean verbose) {        
        List<String> notPersisted = new ArrayList<String>();
        notPersisted.add("assessmentItem");
        notPersisted.add("assessmentFamily");
        notPersisted.add("assessmentPeriodDescriptor");
        notPersisted.add("courseOffering");
        notPersisted.add("objectiveAssessment");
        notPersisted.add("performanceLevelDescriptor");
        notPersisted.add("serviceDescriptor");
        notPersisted.add("studentAssessmentItem");
        notPersisted.add("studentObjectiveAssessment");
        
        String eol = System.getProperty("line.separator");
        List<String> persisted = new ArrayList<String>();
        List<String> ignored = new ArrayList<String>();
        persisted.addAll(counts.keySet());
        persisted.removeAll(notPersisted);
        ignored.addAll(notPersisted);
        Collections.sort(persisted);
        Collections.sort(ignored);
        
        String dataSetSize = parseDirectory(directory);
        String expectedFile = directory + dataSetSize + "_expected.txt";
        
        try {    
            if (verbose) {
                log.println(" [file] Writing to file: " + expectedFile);
            }
            
            FileWriter writer = new FileWriter(expectedFile);
            
            writer.write(" # actual record count: " + String.valueOf(total) + eol);
            writer.write(" # entities not persisted:" + eol);
            for (int i = 0; i < ignored.size(); i++) {
                if (counts.containsKey(ignored.get(i))) {
                    writer.write(" # - " + ignored.get(i) + " --> count: " + String.valueOf(counts.get(ignored.get(i))) + eol);
                }
            }
            
            writer.write(" \"" + dataSetSize + "\" => {" + eol);
            for (int i = 0; i < persisted.size(); i++) {
                if (counts.containsKey(persisted.get(i))) {
                    writer.write("  \"" + persisted.get(i) + "\"=>" + String.valueOf(counts.get(persisted.get(i))));
                    if (i == persisted.size() - 1) {
                        writer.write(eol);
                    } else {
                        writer.write("," + eol);
                    }
                }
            }
            writer.write(" }" + eol);
            
            if (verbose) {
                log.println(" [file] Closing file handle: " + expectedFile);
            }
            
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    private static String parseDirectory(String directory) {
        String[] splitByParen = directory.split("/");
        return splitByParen[splitByParen.length-1];
    }
    
    public static void main(String[] args) {
        String directory = "C:/tmp/SampleDataSets/Medium/";
        PrintStream print = System.out;
        boolean verbose = true;
        long start1 = new DateTime().getMillis();
        Pair<Long, Map<String, Long>> counts = count(directory, print, verbose);
        long end1 = new DateTime().getMillis();
        print.println(" [info] total record count for data: " + counts.getLeft());
        print.println(" [info] time taken: " + ((end1 - start1) / 1000) + " seconds.");
        print.println(" [info] writing _expected file...");
        
        long start2 = new DateTime().getMillis();
        write(directory, counts.getLeft(), mapEntityCounts(directory, counts.getRight(), print, verbose), print, verbose);
        long end2 = new DateTime().getMillis();
        print.println(" [info] time taken: " + ((end2 - start2) / 1000) + " seconds.");
    }    
}
