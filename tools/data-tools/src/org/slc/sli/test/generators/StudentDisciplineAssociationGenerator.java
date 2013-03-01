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

import java.util.Collection;
import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.SLCDisciplineIncident;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncidentReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentDiscipline;
import org.slc.sli.test.edfi.entities.SLCStudentDisciplineIncidentAssociation;
import org.slc.sli.test.edfi.entities.SLCStudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentParticipationCodeType;
import org.slc.sli.test.edfi.entities.SLCStudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;
import org.slc.sli.test.utils.InterchangeWriter;

/**
* Generates StudentDisciplineIncidentAssociation data
*
* @author slee
*
*/
public class StudentDisciplineAssociationGenerator {

    /**
     * Generates a list of StudentDisciplineIncidentAssociation from a DisciplineIncidentMeta.
     *
     * @param meta
     *
     * @return <code>List<StudentDisciplineIncidentAssociation></code>
     */
//    public static List<StudentDisciplineIncidentAssociation> generateLowFi(DisciplineIncidentMeta meta) {
     public static void generateLowFi(InterchangeWriter<InterchangeStudentDiscipline> iWriter, DisciplineIncidentMeta meta) {
        String disciplineIncidentId = meta.id;
        Collection<String> studentIds = meta.studentIds;

//        List<StudentDisciplineIncidentAssociation> list = new ArrayList<StudentDisciplineIncidentAssociation>(studentIds.size());

        for (String studentId : studentIds) {
//            list.add(generateLowFi(studentId, disciplineIncidentId));
            SLCStudentDisciplineIncidentAssociation retVal = generateLowFi(studentId, disciplineIncidentId, meta.schoolId);
            QName qName = new QName("http://ed-fi.org/0100", "StudentDisciplineIncidentAssociation");
            JAXBElement<SLCStudentDisciplineIncidentAssociation> jaxbElement = new JAXBElement<SLCStudentDisciplineIncidentAssociation>(qName,SLCStudentDisciplineIncidentAssociation.class,retVal);
            iWriter.marshal(jaxbElement);
        }

//        return list;
    }

    /**
     * Generates a StudentDisciplineIncidentAssociation between a student and a disciplineIncident.
     *
     * @param studentId
     * @param disciplineIncidentId
     *
     * @return <code>StudentDisciplineIncidentAssociation</code>
     */
    public static SLCStudentDisciplineIncidentAssociation generateLowFi(String studentId, String disciplineIncidentId,
            String schoolId) {

        SLCStudentDisciplineIncidentAssociation assoc = new SLCStudentDisciplineIncidentAssociation();

        assoc.setStudentParticipationCode(GeneratorUtils.generateStudentParticipationCodeType());

        // construct and add the student reference
        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        srt.setStudentIdentity(sit);
        assoc.setStudentReference(srt);

        // construct and add the disciplineIncident Reference
        SLCDisciplineIncidentReferenceType dirt = DisciplineIncidentGenerator.generateReference(disciplineIncidentId,
                schoolId);

        assoc.setDisciplineIncidentReference(dirt);

        return assoc;
    }

    /**
     * Generates a StudentDisciplineIncidentAssociation between a student and a disciplineIncident.
     *
     * @param studentId
     * @param disciplineIncident
     *
     * @return <code>StudentDisciplineIncidentAssociation</code>
     */
    public static SLCStudentDisciplineIncidentAssociation generateLowFi(String studentId,
            SLCDisciplineIncident disciplineIncident, String schoolId) {

        SLCStudentDisciplineIncidentAssociation assoc = new SLCStudentDisciplineIncidentAssociation();

        assoc.setStudentParticipationCode(GeneratorUtils.generateStudentParticipationCodeType());

        // construct and add the student reference
        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        srt.setStudentIdentity(sit);
        assoc.setStudentReference(srt);

        // construct and add the disciplineIncident Reference
        SLCDisciplineIncidentReferenceType dirt = DisciplineIncidentGenerator.generateReference(
                disciplineIncident.getIncidentIdentifier(), schoolId);
        assoc.setDisciplineIncidentReference(dirt);

        return assoc;
    }

    public SLCStudentDisciplineIncidentAssociation generate(String studentDisciplineId, String delimiter) {
        SLCStudentDisciplineIncidentAssociation studentDisciplineAssociation = new SLCStudentDisciplineIncidentAssociation();

        try {
            Random random = new Random(31);

            String studentId = studentDisciplineId.split(delimiter)[0];
            String discId = studentDisciplineId.split(delimiter)[2];

            SLCStudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            studentDisciplineAssociation.setStudentReference(srt);

            SLCDisciplineIncidentReferenceType dirt = DisciplineGenerator.getDisciplineIncidentReferenceType(discId, "ThisStateID");
            studentDisciplineAssociation.setDisciplineIncidentReference(dirt);

            StudentParticipationCodeType spcType = null;
            int randInt4 = random.nextInt(4);
                 if (randInt4 == 0) spcType = StudentParticipationCodeType.PERPETRATOR;
            else if (randInt4 == 1) spcType = StudentParticipationCodeType.REPORTER;
            else if (randInt4 == 2) spcType = StudentParticipationCodeType.VICTIM;
            else if (randInt4 == 3) spcType = StudentParticipationCodeType.WITNESS;
            studentDisciplineAssociation.setStudentParticipationCode(spcType);

            // Behaviors (optional)

            // Secondary Behaviors (optional)

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentDisciplineAssociation;
    }


}
