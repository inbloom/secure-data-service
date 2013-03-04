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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.edfi.entities.Staff;
import org.slc.sli.test.edfi.entities.SLCStaffEducationOrgAssignmentAssociation;
import org.slc.sli.test.edfi.entities.StaffEducationOrgEmploymentAssociation;
import org.slc.sli.test.edfi.entities.SLCStaffProgramAssociation;
import org.slc.sli.test.edfi.entities.Teacher;
import org.slc.sli.test.edfi.entities.SLCTeacherSchoolAssociation;
import org.slc.sli.test.edfi.entities.SLCTeacherSectionAssociation;
import org.slc.sli.test.edfi.entities.meta.StaffMeta;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.StaffProgramAssociationGenerator;
import org.slc.sli.test.generators.StaffEdOrgAssignmentAssociationGenerator;
import org.slc.sli.test.generators.StaffEdOrgEmploymentAssociationGenerator;
import org.slc.sli.test.generators.StaffGenerator;
import org.slc.sli.test.generators.TeacherGenerator;
import org.slc.sli.test.generators.TeacherSchoolAssociationGenerator;
import org.slc.sli.test.generators.TeacherSectionAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Staff Association Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author dduran
 *
 */
public class InterchangeStaffAssociationGenerator {

    /**
     * Sets up a new Staff Association Interchange and populates it.
     *
     * @return
     * @throws Exception
     */
    public static void generate(InterchangeWriter<InterchangeStaffAssociation> iWriter) throws Exception {

        writeEntitiesToInterchange(iWriter);

    }

    /**
     * Generate the individual Teacher Association entities.
     *
     * @param interchangeObjects
     * @throws Exception
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStaffAssociation> iWriter) throws Exception {

        generateTeachers(iWriter, MetaRelations.TEACHER_MAP.values());

        generateTeacherSchoolAssoc(iWriter, MetaRelations.TEACHER_MAP.values());

        generateTeacherSectionAssoc(iWriter, MetaRelations.TEACHER_MAP.values());

        generateStaff(iWriter, MetaRelations.STAFF_MAP.values());

        // TODO: remove when we support (post-alpha?)
        //generateStaffEdOrgEmploymentAssoc(interchangeObjects, MetaRelations.STAFF_MAP.values());

        generateStaffEdOrgAssignmentAssoc(iWriter, MetaRelations.STAFF_MAP.values());

        generateStaffProgramAssoc(iWriter, MetaRelations.PROGRAM_MAP.values());
    }

    /**
     * Loops all teachers and, using a Teacher Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param teacherMetas
     * @throws Exception
     */
    private static void generateTeachers(InterchangeWriter<InterchangeStaffAssociation> iWriter, Collection<TeacherMeta> teacherMetas) throws Exception {
        long startTime = System.currentTimeMillis();

        for (TeacherMeta teacherMeta : teacherMetas) {

            Teacher teacher;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                teacher = null;
            } else {
                //teacher = TeacherGenerator.generateLowFi(teacherMeta.id);
                teacher = TeacherGenerator.generateMediumFi(teacherMeta.id);
            }


            iWriter.marshal(teacher);
        }

        System.out.println("generated " + teacherMetas.size() + " Teacher objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateTeacherSchoolAssoc(InterchangeWriter<InterchangeStaffAssociation> iWriter, Collection<TeacherMeta> teacherMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (TeacherMeta teacherMeta : teacherMetas) {
            for (String schoolId : teacherMeta.schoolIds) {

                SLCTeacherSchoolAssociation teacherSchool;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    teacherSchool = null;
                } else {
                    teacherSchool = TeacherSchoolAssociationGenerator.generateLowFi(teacherMeta, schoolId);
                }

                iWriter.marshal(teacherSchool);

                objGenCounter++;
            }
        }

        System.out.println("generated " + objGenCounter + " TeacherSchoolAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateTeacherSectionAssoc(InterchangeWriter<InterchangeStaffAssociation> iWriter,
            Collection<TeacherMeta> teacherMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (TeacherMeta teacherMeta : teacherMetas) {
            for (String sectionId : teacherMeta.sectionIds) {

                SLCTeacherSectionAssociation teacherSection;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    teacherSection = null;
                } else {
                    teacherSection = TeacherSectionAssociationGenerator.generateLowFi(teacherMeta, sectionId);
                }

                iWriter.marshal(teacherSection);

                objGenCounter++;
            }
        }

        System.out.println("generated " + objGenCounter + " TeacherSectionAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all staff and, using a Staff Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param teacherMetas
     * @throws Exception
     */
    private static void generateStaff(InterchangeWriter<InterchangeStaffAssociation> iWriter, Collection<StaffMeta> staffMetas) throws Exception {
        long startTime = System.currentTimeMillis();

        for (StaffMeta staffMeta : staffMetas) {

            Staff staff;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                staff = null;
            } else {
                //staff = StaffGenerator.generateLowFi(staffMeta.id);
                staff = StaffGenerator.generateMediumFi(staffMeta.id);

            }


            iWriter.marshal(staff);
        }

        System.out.println("generated " + staffMetas.size() + " Staff objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateStaffEdOrgEmploymentAssoc(InterchangeWriter<InterchangeStaffAssociation> iWriter,
            Collection<StaffMeta> staffMetas) {
        long startTime = System.currentTimeMillis();

        for (StaffMeta staffMeta : staffMetas) {

            StaffEducationOrgEmploymentAssociation staffEdOrgEmploymentAssoc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                staffEdOrgEmploymentAssoc = null;
            } else {
                staffEdOrgEmploymentAssoc = StaffEdOrgEmploymentAssociationGenerator.generateLowFi(staffMeta);
            }


            iWriter.marshal(staffEdOrgEmploymentAssoc);
        }

        System.out.println("generated " + staffMetas.size() + " StaffEducationOrgEmploymentAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateStaffEdOrgAssignmentAssoc(InterchangeWriter<InterchangeStaffAssociation> iWriter,
            Collection<StaffMeta> staffMetas) {
        long startTime = System.currentTimeMillis();

        for (StaffMeta staffMeta : staffMetas) {

            SLCStaffEducationOrgAssignmentAssociation staffEdOrgAssignmentAssoc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                staffEdOrgAssignmentAssoc = null;
            } else {
                staffEdOrgAssignmentAssoc = StaffEdOrgAssignmentAssociationGenerator.generateLowFi(staffMeta);
            }


            iWriter.marshal(staffEdOrgAssignmentAssoc);
        }

        System.out.println("generated " + staffMetas.size() + " StaffEducationOrgAssignmentAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Generate StaffProgramAssociation objects
     *
     * @param interchangeObjects
     * @param staffMetas
     */
    private static void generateStaffProgramAssoc(InterchangeWriter<InterchangeStaffAssociation> iWriter,
            Collection<ProgramMeta> programMetas) {
        long startTime = System.currentTimeMillis();
        long count = 0;

        for (ProgramMeta programMeta : programMetas) {
            List<SLCStaffProgramAssociation> staffProgramAssociations = null;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                staffProgramAssociations = new ArrayList<SLCStaffProgramAssociation>();
            } else {
                staffProgramAssociations = StaffProgramAssociationGenerator.generateLowFi(programMeta);
            }
            for (SLCStaffProgramAssociation staffProgram : staffProgramAssociations) {
                iWriter.marshal(staffProgram);
            }
            count += staffProgramAssociations.size();
        }

        System.out.println("generated " + count + " StaffProgramAssociation objects in: "
                + (System.currentTimeMillis() - startTime));

    }
}
