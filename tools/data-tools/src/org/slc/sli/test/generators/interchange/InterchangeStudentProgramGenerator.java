package org.slc.sli.test.generators.interchange;

import static org.slc.sli.test.utils.InterchangeWriter.REPORT_INDENTATION;

import java.util.Collection;
import java.util.List;
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

        System.out.println(interchange.getClass().getSimpleName() + ": started");

        addEntitiesToInterchange(interchangeObjects);

        System.out.println(interchange.getClass().getSimpleName() + ": generated " + interchangeObjects.size() + 
                " entries in " + (System.currentTimeMillis() - startTime) + "\n");
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
        long startTime = System.currentTimeMillis();
        long count = 0;

        for (ProgramMeta programMeta : programMetas) {

            count += generateStudentProgramAssoc(interchangeObjects, programMeta);

            // StaffProgramAssociation is not included in any EdFi interchanges; it is a bug in edfi. 
            // It probably should belong to the student-program interchange. 
            // generateStaffProgramAssoc(interchangeObjects, programMeta);
        }
        
        System.out.println(REPORT_INDENTATION + "generated " + count + " StudentProgramAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static long generateStudentProgramAssoc(List<Object> interchangeObjects, ProgramMeta programMeta) {

        List<StudentProgramAssociation> retVal;

        if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            // if mediumFi requirements extend beyond the lowFi generator, implement and call it here
            retVal = StudentProgramAssociationGenerator.generateLowFi(programMeta);
        } else {
            retVal = StudentProgramAssociationGenerator.generateLowFi(programMeta);
        }
        interchangeObjects.addAll(retVal);
        return retVal.size();
    }

    /**
     * Loops service descriptor and populates the interchange.
     *
     * @param interchangeObjects
     */
    private static void generateServiceDescriptor(List<Object> interchangeObjects) {
        long startTime = System.currentTimeMillis();
        long count = 0;

        ObjectFactory factory = new ObjectFactory();
        for (ProgramGenerator.ServiceDescriptor serviceDescriptor : ProgramGenerator.ServiceDescriptor.values()) {
            ServiceDescriptor sc = factory.createServiceDescriptor();
            sc.setCodeValue(serviceDescriptor.getCodeValue());
            sc.setDescription(serviceDescriptor.getDescription());
            sc.setShortDescription(serviceDescriptor.getShortDescription());
            sc.setServiceCategory(serviceDescriptor.getServiceCategory());
            interchangeObjects.add(sc);
        }
        
        System.out.println(REPORT_INDENTATION + "generated " + count + " ServiceDescriptor objects in: "
                + (System.currentTimeMillis() - startTime));
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
