/**
 *
 */
package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudentParent;
import org.slc.sli.test.edfi.entities.Parent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.StudentParentAssociation;
import org.slc.sli.test.edfi.entities.meta.ParentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentParentAssociationMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.FastStudentGenerator;
import org.slc.sli.test.generators.MediumStudentGenerator;
import org.slc.sli.test.generators.ParentGenerator;
import org.slc.sli.test.generators.StudentParentAssociationGenerator;
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
    public static InterchangeStudentParent generate()  throws Exception  {

        InterchangeStudentParent interchange = new InterchangeStudentParent();
        List<Object> interchangeObjects = interchange.getStudentOrParentOrStudentParentAssociation();

        addEntitiesToInterchange(interchangeObjects);
        
        return interchange;
    }

    /**
     * Generate the individual parent Association entities.
     *
     * @param interchangeObjects
     * @throws Exception 
     */

    private static void addEntitiesToInterchange(List<Object> interchangeObjects) throws Exception {

        generateStudents(interchangeObjects, MetaRelations.STUDENT_MAP.values());

        generateParents(interchangeObjects, MetaRelations.PARENT_MAP.values());

        generateParentStudentAssoc(interchangeObjects, MetaRelations.STUDENT_PARENT_MAP.values());


    }


    /**
     * Loops all students and, using an Fast Student Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param studentMetas
     * @throws Exception 
     */
    private static void generateStudents(List<Object> interchangeObjects, Collection<StudentMeta> studentMetas) {
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

            interchangeObjects.add(student);

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

    private static void generateParents(List<Object> interchangeObjects, Collection<ParentMeta> parentMetas ) throws Exception {


        long startTime = System.currentTimeMillis();

        for (ParentMeta parentMeta : parentMetas) {
            Parent parent;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                parent = null;
            } else {
                //parent = ParentGenerator.generate(parentMeta.id, parentMeta.isMale);
            	parent = ParentGenerator.generateMediumFi(parentMeta.id, parentMeta.isMale);
            }

            interchangeObjects.add(parent);
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

    private static void generateParentStudentAssoc(List<Object> interchangeObjects, Collection<StudentParentAssociationMeta> studentParentAssociationMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;

        for (StudentParentAssociationMeta studentParentAssociationMeta : studentParentAssociationMetas) {

                StudentParentAssociation studentParent;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentParent = null;
                }
                else {
                    studentParent = StudentParentAssociationGenerator.generateLowFi(studentParentAssociationMeta.parentIds,studentParentAssociationMeta.isMale, studentParentAssociationMeta.studentIds);
                }
                interchangeObjects.add(studentParent);

                objGenCounter++;


         }

        System.out.println("generated " + objGenCounter + " StudentParentAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
