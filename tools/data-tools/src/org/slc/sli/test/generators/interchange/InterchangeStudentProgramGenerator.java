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


package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.slc.sli.test.edfi.entities.InterchangeStudentProgram;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.StudentProgramAssociation;
import org.slc.sli.test.edfi.entities.ServiceDescriptor;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.generators.ProgramGenerator;
import org.slc.sli.test.generators.StudentProgramAssociationGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Student Program Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author syau
 *
 */
public class InterchangeStudentProgramGenerator {

    /**
     * Sets up a new Student Program Interchange and populates it.
     *
     * @return
     */
    public static InterchangeStudentProgram generate() {
        long startTime = System.currentTimeMillis();

        InterchangeStudentProgram interchange = new InterchangeStudentProgram();
        List<Object> interchangeObjects = interchange.getStudentProgramAssociationOrStudentSpecialEdProgramAssociationOrRestraintEvent();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated " + interchangeObjects.size() + " InterchangeStudentProgram entries in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    /**
     * Generate the individual Student Association entities.
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateServiceDescriptor(interchangeObjects);
        generateProgramAssocs(interchangeObjects, MetaRelations.PROGRAM_MAP.values());

    }

    /**
     * Loops student-program associations and populates
     * the interchange.
     *
     * @param interchangeObjects
     * @param programMetas
     */
    private static void generateProgramAssocs(List<Object> interchangeObjects, Collection<ProgramMeta> programMetas) {

        for (ProgramMeta programMeta : programMetas) {

            generateStudentProgramAssoc(interchangeObjects, programMeta);

            // StaffProgramAssociation is not included in any EdFi interchanges; it is a bug in edfi. 
            // It probably should belong to the student-program interchange. 
            // generateStaffProgramAssoc(interchangeObjects, programMeta);
        }

    }

    private static void generateStudentProgramAssoc(List<Object> interchangeObjects, ProgramMeta programMeta) {

        List<StudentProgramAssociation> retVal;

        if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            retVal = new ArrayList<StudentProgramAssociation> ();
        } else {
            retVal = StudentProgramAssociationGenerator.generateLowFi(programMeta);
        }
        interchangeObjects.addAll(retVal);
    }

    /**
     * Loops service descriptor and populates the interchange.
     *
     * @param interchangeObjects
     */
    private static void generateServiceDescriptor(List<Object> interchangeObjects) {
        ObjectFactory factory = new ObjectFactory();
        for (ProgramGenerator.ServiceDescriptor serviceDescriptor : ProgramGenerator.ServiceDescriptor.values()) {
            ServiceDescriptor sc = factory.createServiceDescriptor();
            sc.setCodeValue(serviceDescriptor.getCodeValue());
            sc.setDescription(serviceDescriptor.getDescription());
            sc.setShortDescription(serviceDescriptor.getShortDescription());
            sc.setServiceCategory(serviceDescriptor.getServiceCategory());
            interchangeObjects.add(sc);
        }
    }
    
//    private static void generateStaffProgramAssoc(List<Object> interchangeObjects, ProgramMeta programMeta) {
//
//        List<StaffProgramAssociation> retVal;
//
//        if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
//            retVal = new ArrayList<StaffProgramAssociation> ();
//        } else {
//            retVal = StaffProgramAssociationGenerator.generateLowFi(programMeta);
//        }
//        interchangeObjects.addAll(retVal);
//    }

}
