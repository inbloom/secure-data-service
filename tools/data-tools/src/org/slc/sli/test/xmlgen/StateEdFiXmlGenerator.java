/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.xmlgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;


import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.edfi.entities.InterchangeStudentAssessment;
import org.slc.sli.test.edfi.entities.InterchangeStudentAttendance;
import org.slc.sli.test.edfi.entities.InterchangeStudentCohort;
import org.slc.sli.test.edfi.entities.InterchangeStudentDiscipline;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.InterchangeStudentGrade;
import org.slc.sli.test.edfi.entities.InterchangeStudentParent;
import org.slc.sli.test.edfi.entities.InterchangeStudentProgram;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.interchange.InterchangeAssessmentMetadataGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgGenerator;
import org.slc.sli.test.generators.interchange.InterchangeMasterScheduleGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStaffAssociationGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentAssessmentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentAttendanceGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentCohortGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentDisciplineGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentEnrollmentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentGradeGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentParentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentProgramGenerator;
import org.slc.sli.test.utils.DataUtils;
import org.slc.sli.test.utils.EdfiStats;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.utils.JaxbUtils;
import org.slc.sli.test.utils.ValidateSchema;

/**
 * Code to generate referentially correct interchanges that are built
 * as a depth-first walk of the dependency graph beginning with
 * StateEducationAgency.
 *
 * @author dduran
 *
 */
public class StateEdFiXmlGenerator {

	
    /**
     * used by interchange generators to determine which entity generator to invoke
     */
    //public static String fidelityOfData = "medium";
    public static String fidelityOfData = "low";
    
//    public static String rootOutputPath = "./data";
    /**
     * Currently generates:
     * - edOrg
     * - edOrgCalendar
     * - masterSchedule
     * - staffAssociation
     * - student
     * - studentEnrollment
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
   
        processProgramArguments(args);
        
        MetaRelations.construct();
        
        generateAndMarshalInterchanges();
        
        ValidateSchema.check(MetaRelations.rootOutputPath);
        
        EdfiStats.generateStats(MetaRelations.rootOutputPath);
    
    }


    
    private static void processProgramArguments(String[] args) {

        if (args.length > 0 && ("low".equals(args[0]) || "medium".equals(args[0]))) {
            fidelityOfData = args[0];
        }
        if (args.length > 1) {
        	MetaRelations.rootOutputPath = args[1];
        }
        System.out.println("will use " + fidelityOfData + " fidelity data generators.");

        if (new File(MetaRelations.rootOutputPath).mkdirs()) {
            System.out.println("created directory: " + MetaRelations.rootOutputPath);
        }
        System.out.println("root output path: " + MetaRelations.rootOutputPath);
    }

    private static void generateAndMarshalInterchanges() throws Exception {

       
       edOrg();
       
       edOrgCalendar();
       
       masterSchedule();

       staffAssociation();

       studentParent();

       studentEnrollment();

       studentProgram();

       studentCohort();

       studentDiscipline();

       studentAttendance();

       assessmentMetaData();

       studentAssessment();

       studentGrade();

    }

    /**
     * Generate InterchangeEducationOrganization data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrg() throws Exception {
    	
    	 InterchangeWriter<InterchangeEducationOrganization> iWriter = new InterchangeWriter<InterchangeEducationOrganization>(InterchangeEducationOrganization.class);
    	 InterchangeEdOrgGenerator.generate(iWriter);
         iWriter.close();

         DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "EducationOrganization", iWriter.getXmlFilePath());

    }

    /**
     * Generate InterchangeEducationOrgCalendar data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrgCalendar() throws Exception {
    	
    	InterchangeWriter<InterchangeEducationOrgCalendar> iWriter = new InterchangeWriter<InterchangeEducationOrgCalendar>(InterchangeEducationOrgCalendar.class);

    	InterchangeEdOrgCalGenerator.generate(iWriter);
    	iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "EducationOrgCalendar", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeMasterSchedule data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void masterSchedule() throws Exception {
    	  InterchangeWriter<InterchangeMasterSchedule> iWriter = 
                  new InterchangeWriter<InterchangeMasterSchedule>(InterchangeMasterSchedule.class);
          InterchangeMasterScheduleGenerator.generate(iWriter);
          iWriter.close();

          DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "MasterSchedule", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStaffAssociation data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void staffAssociation() throws Exception {
        
        InterchangeWriter<InterchangeStaffAssociation> iWriter = 
                new InterchangeWriter<InterchangeStaffAssociation>(InterchangeStaffAssociation.class);
        InterchangeStaffAssociationGenerator.generate(iWriter);
        iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StaffAssociation", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentParent data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */

    private static void studentParent() throws Exception {
        
        InterchangeWriter<InterchangeStudentParent> iWriter = 
                new InterchangeWriter<InterchangeStudentParent>(InterchangeStudentParent.class);
        InterchangeStudentParentGenerator.generate(iWriter);
        iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentParent", iWriter.getXmlFilePath());

    }

    /**
     * Generate InterchangeStudentEnrollment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentEnrollment() throws Exception {
        
        InterchangeWriter<InterchangeStudentEnrollment> iWriter = 
                new InterchangeWriter<InterchangeStudentEnrollment>(InterchangeStudentEnrollment.class);
        InterchangeStudentEnrollmentGenerator.generate(iWriter);
        iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentEnrollment", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentAttendance data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentAttendance() throws Exception {

        InterchangeWriter<InterchangeStudentAttendance> iWriter = 
                new InterchangeWriter<InterchangeStudentAttendance>(InterchangeStudentAttendance.class);
        InterchangeStudentAttendanceGenerator.generate(iWriter);
        iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "Attendance", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentProgram data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentProgram() throws Exception {

        InterchangeWriter<InterchangeStudentProgram> iWriter = 
                new InterchangeWriter<InterchangeStudentProgram>(InterchangeStudentProgram.class);
        InterchangeStudentProgramGenerator.generate(iWriter);
        iWriter.close();
        
        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentProgram",
        		iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentCohort data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentCohort() throws Exception {

        InterchangeWriter<InterchangeStudentCohort> iWriter = 
                new InterchangeWriter<InterchangeStudentCohort>(InterchangeStudentCohort.class);
        InterchangeStudentCohortGenerator.generate(iWriter);
        iWriter.close();
        
        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentCohort",
        		iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentDiscipline data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentDiscipline() throws Exception {
    	
    	InterchangeWriter<InterchangeStudentDiscipline> iWriter = 
                new InterchangeWriter<InterchangeStudentDiscipline>(InterchangeStudentDiscipline.class);
    	InterchangeStudentDisciplineGenerator.generate(iWriter);
    	iWriter.close();
        // TODO: uncomment when ingestion supports this
        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentDiscipline",
        		iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeAssessmentMetadata data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void assessmentMetaData() throws Exception {

    	InterchangeWriter<InterchangeAssessmentMetadata> iWriter = 
                new InterchangeWriter<InterchangeAssessmentMetadata>(InterchangeAssessmentMetadata.class);
    	InterchangeAssessmentMetadataGenerator.generate(iWriter);
        iWriter.close();
    	

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "AssessmentMetadata", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentAssessment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentAssessment() throws Exception {

        InterchangeWriter<InterchangeStudentAssessment> iWriter = 
                new InterchangeWriter<InterchangeStudentAssessment>(InterchangeStudentAssessment.class);
        InterchangeStudentAssessmentGenerator.generate(iWriter);
        iWriter.close();

        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentAssessment", iWriter.getXmlFilePath());
    }

    /**
     * Generate InterchangeStudentGrade data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentGrade() throws Exception {

        InterchangeWriter<InterchangeStudentGrade> iWriter = 
                new InterchangeWriter<InterchangeStudentGrade>(InterchangeStudentGrade.class);
        InterchangeStudentGradeGenerator.generate(iWriter);
        iWriter.close();
        
        DataUtils.writeControlFile(MetaRelations.rootOutputPath + "/MainControlFile.ctl", "StudentGrades", iWriter.getXmlFilePath());
    }

}
