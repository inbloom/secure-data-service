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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.DisciplineAction;
import org.slc.sli.test.edfi.entities.DisciplineIncident;
import org.slc.sli.test.edfi.entities.InterchangeStudentDiscipline;
import org.slc.sli.test.edfi.entities.StudentDisciplineIncidentAssociation;
import org.slc.sli.test.edfi.entities.meta.DisciplineActionMeta;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.DisciplineActionGenerator;
import org.slc.sli.test.generators.DisciplineIncidentGenerator;
import org.slc.sli.test.generators.StudentDisciplineAssociationGenerator;
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
    public static InterchangeStudentDiscipline generate() {
        long startTime = System.currentTimeMillis();

        InterchangeStudentDiscipline interchange = new InterchangeStudentDiscipline();
        List<Object> interchangeObjects = interchange.getDisciplineIncidentOrStudentDisciplineIncidentAssociationOrDisciplineAction();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated " + interchangeObjects.size() + " InterchangeStudentDiscipline entries in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    /**
     * add related Entities To Interchange.
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateDisciplineIncidentData(interchangeObjects, MetaRelations.DISCIPLINE_INCIDENT_MAP.values());
        generateStudentDisciplineIncidentAssociation(interchangeObjects, MetaRelations.DISCIPLINE_INCIDENT_MAP.values());
        generateDisciplineActionData(interchangeObjects, MetaRelations.DISCIPLINE_ACTION_MAP.values());
    }

    /**
     * Call DisciplineIncidentGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineIncidentMetas
     */
    private static void generateDisciplineIncidentData(List<Object> interchangeObjects, Collection<DisciplineIncidentMeta> disciplineIncidentMetas) {

        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentMetas) {
            DisciplineIncident retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = null;
            } else {
                retVal = DisciplineIncidentGenerator.generateLowFi(disciplineIncidentMeta);
            }
            interchangeObjects.add(retVal);
        }
        
    }

    /**
     * Call DisciplineActionGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineActionMetas
     */
    private static void generateDisciplineActionData(List<Object> interchangeObjects, Collection<DisciplineActionMeta> disciplineActionMetas) {

        for (DisciplineActionMeta disciplineActionMeta : disciplineActionMetas) {
            DisciplineAction retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = null;
            } else {
                retVal = DisciplineActionGenerator.generateLowFi(disciplineActionMeta);
            }
            interchangeObjects.add(retVal);
        }
        
    }

    /**
     * Call StaffCohortAssociationGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param disciplineIncidentMetas
     */
    private static void generateStudentDisciplineIncidentAssociation(List<Object> interchangeObjects, Collection<DisciplineIncidentMeta> disciplineIncidentMetas) {

        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentMetas) {
            
            List<StudentDisciplineIncidentAssociation> retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = new ArrayList<StudentDisciplineIncidentAssociation>(0);
            } else {
                retVal = StudentDisciplineAssociationGenerator.generateLowFi(disciplineIncidentMeta);
            }
            interchangeObjects.addAll(retVal);
        }
    }
    
}
