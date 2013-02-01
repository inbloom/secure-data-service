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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.SLCDisciplineAction;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncident;
import org.slc.sli.test.edfi.entities.InterchangeStudentDiscipline;
import org.slc.sli.test.edfi.entities.SLCProgram;
import org.slc.sli.test.edfi.entities.SLCStudentDisciplineIncidentAssociation;
import org.slc.sli.test.edfi.entities.meta.DisciplineActionMeta;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.DisciplineActionGenerator;
import org.slc.sli.test.generators.DisciplineIncidentGenerator;
import org.slc.sli.test.generators.StudentDisciplineAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Student Discipline Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author slee
 *
 */
public class InterchangeStudentDisciplineGenerator {

    /**
     * Sets up a new Student Discipline Interchange and populates it.
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeStudentDiscipline> iWriter) {
        long startTime = System.currentTimeMillis();

        int total = writeEntitiesToInterchange(iWriter);

        System.out.println("generated " + total + " InterchangeStudentDiscipline entries in: "
                + (System.currentTimeMillis() - startTime));

    }

    /**
     * add related Entities To Interchange.
     *
     * @param interchangeObjects
     */
    private static int writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentDiscipline> iWriter) {

    	int total=0;
        total += generateDisciplineIncidentData(iWriter, MetaRelations.DISCIPLINE_INCIDENT_MAP.values());
        total += generateStudentDisciplineIncidentAssociation(iWriter, MetaRelations.DISCIPLINE_INCIDENT_MAP.values());
        total += generateDisciplineActionData(iWriter, MetaRelations.DISCIPLINE_ACTION_MAP.values());
        return total;
    }

    /**
     * Call DisciplineIncidentGenerator to populates data into
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineIncidentMetas
     */
    private static int generateDisciplineIncidentData(InterchangeWriter<InterchangeStudentDiscipline> iWriter, Collection<DisciplineIncidentMeta> disciplineIncidentMetas) {

    	int count=0;
        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentMetas) {
            SLCDisciplineIncident retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = null;
            } else {
                retVal = DisciplineIncidentGenerator.generateLowFi(disciplineIncidentMeta);
            }

            iWriter.marshal(retVal);
            count++;
        }
        return count;

    }

    /**
     * Call DisciplineActionGenerator to populates data into
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineActionMetas
     */
    private static int generateDisciplineActionData(InterchangeWriter<InterchangeStudentDiscipline> iWriter, Collection<DisciplineActionMeta> disciplineActionMetas) {

    	int count=0;
        for (DisciplineActionMeta disciplineActionMeta : disciplineActionMetas) {
            SLCDisciplineAction retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = null;
            } else {
                retVal = DisciplineActionGenerator.generateLowFi(disciplineActionMeta);
            }

            iWriter.marshal(retVal);
            count++;

        }
        return count;
    }

    /**
     * Call StudentDisciplineIncidentAssociationGenerator to populates data into
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineIncidentMetas
     */
    private static int generateStudentDisciplineIncidentAssociation(InterchangeWriter<InterchangeStudentDiscipline> iWriter, Collection<DisciplineIncidentMeta> disciplineIncidentMetas) {

    	int count=0;
        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentMetas) {

            List<SLCStudentDisciplineIncidentAssociation> retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = new ArrayList<SLCStudentDisciplineIncidentAssociation>(0);
            } else {
                StudentDisciplineAssociationGenerator.generateLowFi(iWriter,disciplineIncidentMeta);
            }
            count++;
        }
        return count;
    }

}
