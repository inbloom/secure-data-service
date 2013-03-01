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


package org.slc.sli.test.generators;

import java.util.Random;
import java.util.List;
import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.ProgramSponsorType;
import org.slc.sli.test.edfi.entities.ProgramType;
import org.slc.sli.test.edfi.entities.SLCProgram;
import org.slc.sli.test.edfi.entities.ServiceDescriptorType;

public class ProgramGenerator {

    private static final Logger log = Logger.getLogger(ProgramGenerator.class);
    
    private static Random rand = new Random(31);

    // Descriptor for program services. 
    public enum ServiceDescriptor {
        REMEDIAL("Service 1", "Remedial Service", "Remedial service for students with learning disabilities.", "Remedial"),
        ARTANDMUSIC("Service 2", "Art and Music", "Service for arts and music education.", "Art"),
        SPORTS("Service 3", "Sports", "Sport programs", "Sports"),
        EXTRACURRICULAR("Service 4", "Extra-Curricular Activites", "Extra-curricular activity service.", "Extra");
        
        String codeValue;
        String shortDescription;
        String description;
        String serviceCategory;
        
        ServiceDescriptor (String cv, String sd, String d, String sc) {
            codeValue = cv;
            shortDescription = sd;
            description = d;
            serviceCategory = sc;
        }
        public String getCodeValue() { return codeValue; }
        public String getShortDescription() { return shortDescription; }
        public String getDescription() { return description; }
        public String getServiceCategory() { return serviceCategory; }
    }

    public static SLCProgram generate(String programId) {
        return generateLowFi(programId);
    }
    
    public static SLCProgram generateLowFi(String programId) {
        SLCProgram program = new SLCProgram();
        program.setId(programId);
        program.setProgramId(programId);
        
        int programTypeIndx = Math.abs(rand.nextInt() % ProgramType.values().length);
        if( programTypeIndx == 23)
            programTypeIndx = programTypeIndx + 1;
        if( programTypeIndx == 12)
            programTypeIndx = programTypeIndx + 1;
        if( programTypeIndx == 15)
            programTypeIndx = programTypeIndx + 1;
        
           
        program.setProgramType(ProgramType.values()[programTypeIndx]);//edfi schema and sli schema has two set vale of programType, donot use value not in sli schema
       
        int programSponsorTypeIndx = Math.abs(rand.nextInt() % ProgramSponsorType.values().length);
        program.setProgramSponsor(ProgramSponsorType.values()[programSponsorTypeIndx]);
        
        List<ServiceDescriptorType> services = program.getServices();

        // construct and add the service descriptor references
        double probForServiceInAProgram = 1.0D / ServiceDescriptor.values().length;
        for(ServiceDescriptor serviceDescriptor : ServiceDescriptor.values()) {
            if (rand.nextDouble() < probForServiceInAProgram) {
                ServiceDescriptorType serviceDescriptorType = new ServiceDescriptorType();
               // JAXBElement<String> serviceDescriptorCode =  factory.createServiceDescriptorTypeCodeValue(serviceDescriptor.codeValue);
               // serviceDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(serviceDescriptorCode);
                serviceDescriptorType.setCodeValue(serviceDescriptor.codeValue);
                services.add(serviceDescriptorType);
            }
        }

        return program;
    }
}
