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
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.StudentProgramAssociation;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.FastStudentGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Master Schedule Interchange as derived from the variable:
 * - studentMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeStudentGenerator {

    /**
     * Sets up a new Student Interchange and populates it
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeStudent> iWriter) {



        writeEntitiesToInterchange(iWriter);

    }

    /**
     * Generates the individual entities that can generate a Student
     *
     * @param interchangeObjects
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStudent> iWriter) {

        generateStudents(iWriter, MetaRelations.STUDENT_MAP.values());

    }

    /**
     * Loops all students and, using an Fast Student Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param studentMetas
     */
    private static void generateStudents(InterchangeWriter<InterchangeStudent> iWriter, Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        for (StudentMeta studentMeta : studentMetas) {

            Student student;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                student = null;
            } else {
                student = FastStudentGenerator.generateLowFi(studentMeta.id);
            }


            iWriter.marshal(student);

        }

        System.out.println("generated " + studentMetas.size() + " Student objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
