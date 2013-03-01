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
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.InterchangeStudentProgram;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.SLCStudentProgramAssociation;
import org.slc.sli.test.edfi.entities.ServiceDescriptor;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.generators.ProgramGenerator;
import org.slc.sli.test.generators.StudentProgramAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
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
    public static void generate(InterchangeWriter<InterchangeStudentProgram> iWriter) {
        long startTime = System.currentTimeMillis();

//        InterchangeStudentProgram interchange = new InterchangeStudentProgram();
//        List<Object> interchangeObjects = interchange.getStudentProgramAssociationOrStudentSpecialEdProgramAssociationOrRestraintEvent();

        int total = writeEntitiesToInterchange(iWriter);

        System.out.println("generated " + total + " InterchangeStudentProgram entries in: "
                + (System.currentTimeMillis() - startTime));
        
    }

    /**
     * Generate the individual Student Association entities.
     *
     * @param interchangeObjects
     */
    private static int writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentProgram> iWriter) {

        int total = 0;
        total += generateServiceDescriptor(iWriter);
        total += generateProgramAssocs(iWriter, MetaRelations.PROGRAM_MAP.values());
        return total;

    }

    /**
     * Loops student-program associations and populates
     * the interchange.
     *
     * @param interchangeObjects
     * @param programMetas
     */
    private static int generateProgramAssocs(InterchangeWriter<InterchangeStudentProgram> iWriter, Collection<ProgramMeta> programMetas) {

        int count = 0;
        for (ProgramMeta programMeta : programMetas) {

            count += generateStudentProgramAssoc(iWriter, programMeta);
            

            // StaffProgramAssociation is not included in any EdFi interchanges; it is a bug in edfi. 
            // It probably should belong to the student-program interchange. 
            // generateStaffProgramAssoc(interchangeObjects, programMeta);
        }
        return count;
    }

    private static int generateStudentProgramAssoc(InterchangeWriter<InterchangeStudentProgram> iWriter, ProgramMeta programMeta) {

        int count=0;
        List<SLCStudentProgramAssociation> retVal;

        if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            retVal = new ArrayList<SLCStudentProgramAssociation> ();
        } else {
            count += StudentProgramAssociationGenerator.generateLowFi(iWriter, programMeta);
        }
  
        return count;
    }

    /**
     * Loops service descriptor and populates the interchange.
     *
     * @param interchangeObjects
     */
    private static int generateServiceDescriptor(InterchangeWriter<InterchangeStudentProgram> iWriter) {
        int count = 0;
        ObjectFactory factory = new ObjectFactory();
        for (ProgramGenerator.ServiceDescriptor serviceDescriptor : ProgramGenerator.ServiceDescriptor.values()) {
            ServiceDescriptor sc = factory.createServiceDescriptor();
            sc.setCodeValue(serviceDescriptor.getCodeValue());
            sc.setDescription(serviceDescriptor.getDescription());
            sc.setShortDescription(serviceDescriptor.getShortDescription());
            sc.setServiceCategory(serviceDescriptor.getServiceCategory());
//            interchangeObjects.add(sc);
            
            QName qName = new QName("http://ed-fi.org/0100", "ServiceDescriptor");
            JAXBElement<ServiceDescriptor> jaxbElement = new JAXBElement<ServiceDescriptor>(qName,ServiceDescriptor.class,sc);

            iWriter.marshal(jaxbElement);
        }
        return count;
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
