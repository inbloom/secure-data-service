package org.slc.sli.test.generators;

import java.util.Random;
import java.util.List;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Program;
import org.slc.sli.test.edfi.entities.ProgramSponsorType;
import org.slc.sli.test.edfi.entities.ProgramType;
import org.slc.sli.test.edfi.entities.ServiceDescriptorType;
import org.slc.sli.test.edfi.entities.ObjectFactory;

public class ProgramGenerator {

    private static final Logger log = Logger.getLogger(ProgramGenerator.class);
    
    private static Random rand = new Random();

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

    public static Program generate(String programId) {
        return generateLowFi(programId);
	}
	
	public static Program generateLowFi(String programId) {
        Program program = new Program();
        program.setId(programId);
        program.setProgramId(programId);
        
        int programTypeIndx = Math.abs(rand.nextInt() % ProgramType.values().length);
        program.setProgramType(ProgramType.values()[programTypeIndx]);
        int programSponsorTypeIndx = Math.abs(rand.nextInt() % ProgramSponsorType.values().length);
        program.setProgramSponsor(ProgramSponsorType.values()[programSponsorTypeIndx]);
        
        List<ServiceDescriptorType> services = program.getServices();

        ObjectFactory factory = new ObjectFactory();
        
        // construct and add the service descriptor references
        double probForServiceInAProgram = 1.0D / ServiceDescriptor.values().length;
        for(ServiceDescriptor serviceDescriptor : ServiceDescriptor.values()) {
            if (rand.nextDouble() < probForServiceInAProgram) {
                ServiceDescriptorType serviceDescriptorType = new ServiceDescriptorType();
                JAXBElement<String> serviceDescriptorCode =  factory.createServiceDescriptorTypeCodeValue(serviceDescriptor.codeValue);
                serviceDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(serviceDescriptorCode);
                services.add(serviceDescriptorType);
            }
        }

        return program;
	}
}
