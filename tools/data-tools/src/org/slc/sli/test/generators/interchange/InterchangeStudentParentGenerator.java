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


/**
 *
 */
package org.slc.sli.test.generators.interchange;

import java.util.Collection;

import org.slc.sli.test.edfi.entities.InterchangeStudentParent;
import org.slc.sli.test.edfi.entities.Parent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.SLCStudentParentAssociation;
import org.slc.sli.test.edfi.entities.meta.ParentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentParentAssociationMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.MediumStudentGenerator;
import org.slc.sli.test.generators.ParentGenerator;
import org.slc.sli.test.generators.StudentParentAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;


/**
 * @author lchen
 *
 */

/**
 * Generates the Student Parent Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author lchen
 */

public class InterchangeStudentParentGenerator {

    /**
     * Sets up a new Student Parent Interchange and populates it.
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeStudentParent> iWriter)  throws Exception  {

        writeEntitiesToInterchange(iWriter);

    }

    /**
     * Generate the individual parent Association entities.
     *
     * @param interchangeObjects
     * @throws Exception
     */

    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentParent> iWriter) throws Exception {

        generateStudents(iWriter, MetaRelations.STUDENT_MAP.values());


        if (!MetaRelations.EXCLUDE_PARENTS) {
            generateParents(iWriter, MetaRelations.PARENT_MAP.values());
            //Why duplicate calls?
            //generateParents(iWriter, MetaRelations.PARENT_MAP.values());

            generateParentStudentAssoc(iWriter, MetaRelations.STUDENT_PARENT_MAP.values());
        }
    }


    /**
     * Loops all students and, using an Fast Student Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param studentMetas
     * @throws Exception
     */
    private static void generateStudents(InterchangeWriter<InterchangeStudentParent> iWriter, Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        for (StudentMeta studentMeta : studentMetas) {

            Student student = null;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                try {
                    student = MediumStudentGenerator.generateMediumFi(studentMeta.id);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    student = MediumStudentGenerator.generateMediumFi(studentMeta.id);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }



            iWriter.marshal(student);

        }

        System.out.println("generated " + studentMetas.size() + " Student objects in: "
                + (System.currentTimeMillis() - startTime));
    }


    /**
     * Loops all parents and, using an parent Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param parentMetas
     * @throws Exception
     */

    private static void generateParents(InterchangeWriter<InterchangeStudentParent> iWriter, Collection<ParentMeta> parentMetas ) throws Exception {


        long startTime = System.currentTimeMillis();

        for (ParentMeta parentMeta : parentMetas) {
            Parent parent;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                parent = null;
            } else {
                //parent = ParentGenerator.generate(parentMeta.id, parentMeta.isMale);
                parent = ParentGenerator.generateMediumFi(parentMeta.id, parentMeta.isMale);
            }



            iWriter.marshal(parent);
        }

        System.out.println("generated " + parentMetas.size() + " Parent objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Generate the individual Parent Association entities.
     *
     * @param interchangeObjects
     * @param studentParentAssociationMetas
     */

    private static void generateParentStudentAssoc(InterchangeWriter<InterchangeStudentParent> iWriter, Collection<StudentParentAssociationMeta> studentParentAssociationMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;

        for (StudentParentAssociationMeta studentParentAssociationMeta : studentParentAssociationMetas) {

                SLCStudentParentAssociation studentParent;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentParent = null;
                }
                else {
                    studentParent = StudentParentAssociationGenerator.generateLowFi(studentParentAssociationMeta.parentIds,studentParentAssociationMeta.isMale, studentParentAssociationMeta.studentIds);
                }


                iWriter.marshal(studentParent);

                objGenCounter++;


         }

        System.out.println("generated " + objGenCounter + " StudentParentAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
