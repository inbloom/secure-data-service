/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCEducationServiceCenter;
import org.slc.sli.test.edfi.entities.FeederSchoolAssociation;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.SLCLocalEducationAgency;
import org.slc.sli.test.edfi.entities.SLCCourse;
import org.slc.sli.test.edfi.entities.SLCProgram;
import org.slc.sli.test.edfi.entities.SLCSchool;
import org.slc.sli.test.edfi.entities.School;
import org.slc.sli.test.edfi.entities.SLCStateEducationAgency;
import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.ESCMeta;
import org.slc.sli.test.edfi.entities.meta.LeaMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CourseGenerator;
import org.slc.sli.test.generators.EducationAgencyGenerator;
import org.slc.sli.test.generators.LocalEducationAgencyGenerator;
import org.slc.sli.test.generators.ProgramGenerator;
import org.slc.sli.test.generators.SchoolGenerator;
import org.slc.sli.test.generators.StateEducationAgencyGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates all Education Organizations contained in the variables:
 * - seaMap
 * - leaMap
 * - schoolMap
 * - courseMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeEdOrgGenerator {


    static CourseGenerator gen ;

    static {
        try
        {
            gen = new CourseGenerator(GradeLevelType.SEVENTH_GRADE);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets up a new Education Organization Interchange and populates it
     *
     * @return
     * @throws Exception
     */
    public static void generate(InterchangeWriter<InterchangeEducationOrganization> iWriter) throws Exception {     
        writeEntitiesToInterchange(iWriter);
    }

    /**
     * Generates the individual entities that can be Educational Organizations
     *
     * @param interchangeObjects
     * @throws Exception
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeEducationOrganization> iWriter) throws Exception {

        generateStateEducationAgencies(iWriter, MetaRelations.SEA_MAP.values());

        generateEducationServiceCenters(iWriter, MetaRelations.ESC_MAP.values());
        
        generateFeederSchoolAssociation(iWriter, MetaRelations.SCHOOL_MAP.values());
        
        generateLocalEducationAgencies(iWriter, MetaRelations.LEA_MAP.values());

        generateSchools(iWriter, MetaRelations.SCHOOL_MAP.values());

        generateCourses(iWriter, MetaRelations.COURSE_MAP.values());

        generatePrograms(iWriter, MetaRelations.PROGRAM_MAP.values());

    }

    /**
     * Loops all SEAs and, using an SEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateStateEducationAgencies(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<SeaMeta> seaMetas) {
        long startTime = System.currentTimeMillis();

        for (SeaMeta seaMeta : seaMetas) {

            SLCStateEducationAgency sea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                sea = StateEducationAgencyGenerator.generateLowFi(seaMeta.id, seaMeta);
            } else {
                sea = StateEducationAgencyGenerator.generateLowFi(seaMeta.id, seaMeta);
            }
            iWriter.marshal(sea);
        }

        System.out.println("generated " + seaMetas.size() + " StateEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all ESCs and, using an ESC Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param escMetas
     */
    private static void generateEducationServiceCenters(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<ESCMeta> escMetas) {
        long startTime = System.currentTimeMillis();

        for (ESCMeta escMeta : escMetas) {

            SLCEducationServiceCenter esc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                esc = EducationAgencyGenerator.getEducationServiceCenter(escMeta.id, escMeta.seaId);
            } else {
                esc = EducationAgencyGenerator.getEducationServiceCenter(escMeta.id, escMeta.seaId);
            }
            
            iWriter.marshal(esc);
        }

        System.out.println("generated " + escMetas.size() + " EducationServiceCenter objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    /**
     * Generates FEEDER_RELATIONSHIPS FeederSchoolAssociation between 2 schools using a circular list.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateFeederSchoolAssociation(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<SchoolMeta> schools) {
        long startTime = System.currentTimeMillis();

        List<SchoolMeta> schoolMetas = new LinkedList<SchoolMeta>(schools);
        int schoolCount = schoolMetas.size();
        if(schoolCount > 1) {
            for(int i = 0; i < MetaRelations.FEEDER_RELATIONSHIPS; i++) {
                SchoolMeta feederMeta   = schoolMetas.get(i % schoolCount);
                SchoolMeta receiverMeta = schoolMetas.get((i+ 1) % schoolCount);
                FeederSchoolAssociation fsa = EducationAgencyGenerator.getFeederSchoolAssociation(receiverMeta, feederMeta);
                fsa.setFeederRelationshipDescription("Feeder Relationship " +  i);
          
                iWriter.marshal(fsa);
            }
        }

        System.out.println("generated " + MetaRelations.FEEDER_RELATIONSHIPS + " FeederSchoolAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    /**
     * Loops all LEAs and, using an LEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param leaMetas
     */
    private static void generateLocalEducationAgencies(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<LeaMeta> leaMetas) {
        long startTime = System.currentTimeMillis();

        for (LeaMeta leaMeta : leaMetas) {

            SLCLocalEducationAgency lea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                lea = LocalEducationAgencyGenerator.generateMedFi(leaMeta.id, leaMeta.seaId, leaMeta);
            } else {
                lea = LocalEducationAgencyGenerator.generateMedFi(leaMeta.id, leaMeta.seaId, leaMeta);
            }

            iWriter.marshal(lea);
        }

        System.out.println("generated " + leaMetas.size() + " LocalEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all schools and, using a School Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param schoolMetas
     */
    private static void generateSchools(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<SchoolMeta> schoolMetas) {
        long startTime = System.currentTimeMillis();

        for (SchoolMeta schoolMeta : schoolMetas) {

            SLCSchool school;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                school = null;
            } else {
                school = SchoolGenerator.generateLowFi(schoolMeta.id, schoolMeta.leaId, schoolMeta.programId);
            }


            iWriter.marshal(school);
        }

        System.out.println("generated " + schoolMetas.size() + " School objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all courses and, using a Course Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param courseMetas
     * @throws Exception
     */
    private static void generateCourses(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<CourseMeta> courseMetas) throws Exception {
        long startTime = System.currentTimeMillis();
        for (CourseMeta courseMeta : courseMetas) {

            SLCCourse course;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                //course = CourseGenerator.generateMidumFi(courseMeta.id, courseMeta.schoolId);
                course = null;
            } else {
                //course = CourseGenerator.generateLowFi(courseMeta.id, courseMeta.schoolId);
                course = gen.getCourse(courseMeta.id, courseMeta.schoolId, courseMeta.uniqueCourseId);
            }

            courseMeta.courseCodes.addAll(course.getCourseCode());

            
            iWriter.marshal(course);
        }

        System.out.println("generated " + courseMetas.size() + " Course objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all programs and, using a Program Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param programMetas
     */
    private static void generatePrograms(InterchangeWriter<InterchangeEducationOrganization> iWriter, Collection<ProgramMeta> programMetas) {
        for (ProgramMeta programMeta : programMetas) {

            SLCProgram program;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                program = null;
            } else {
                program = ProgramGenerator.generateLowFi(programMeta.id);
            }
  
            iWriter.marshal(program);
        }
    }
}
