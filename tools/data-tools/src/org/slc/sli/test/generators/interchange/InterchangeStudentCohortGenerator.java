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

import org.slc.sli.test.edfi.entities.Cohort;
import org.slc.sli.test.edfi.entities.InterchangeStudentCohort;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.SLCCohort;
import org.slc.sli.test.edfi.entities.SLCStaffCohortAssociation;
import org.slc.sli.test.edfi.entities.StaffCohortAssociation;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CohortGenerator;
import org.slc.sli.test.generators.StaffCohortAssociationGenerator;
import org.slc.sli.test.generators.StudentCohortAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Student Cohort Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author slee
 *
 */
public class InterchangeStudentCohortGenerator {

    /**
     * Sets up a new Student Cohort Interchange and populates it.
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeStudentCohort> iWriter) {
        long startTime = System.currentTimeMillis();

//        InterchangeStudentCohort interchange = new InterchangeStudentCohort();
//        List<Object> interchangeObjects = interchange.getCohortOrStudentCohortAssociationOrStaffCohortAssociation();

        int total = 0;
        total += writeEntitiesToInterchange(iWriter);

        System.out.println("generated " + total + " InterchangeStudentCohort entries in: "
                + (System.currentTimeMillis() - startTime));
//        return interchange;
    }

    /**
     * add related Entities To Interchange.
     *
     * @param interchangeObjects
     */
    private static int writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentCohort> iWriter) {

    	int total=0;
    	total += generateCohortData(iWriter, MetaRelations.COHORT_MAP.values());
    	total += generateStaffCohortAssociationData(iWriter, MetaRelations.COHORT_MAP.values());
    	total += generateStudentCohortAssociation(iWriter, MetaRelations.COHORT_MAP.values());
    	return total;
    }

    /**
     * Call CohortGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param cohortMetas
     */
    private static int generateCohortData(InterchangeWriter<InterchangeStudentCohort> iWriter, Collection<CohortMeta> cohortMetas) {

    	int count = 0;
        for (CohortMeta cohortMeta : cohortMetas) {
            SLCCohort retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = null;
            } else {
                retVal = CohortGenerator.generateLowFi(cohortMeta);
            }
            
            
            iWriter.marshal(retVal);
            count++;
        }
        return count;
        
    }

    /**
     * Call StaffCohortAssociationGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param cohortMetas
     */
    private static int generateStaffCohortAssociationData(InterchangeWriter<InterchangeStudentCohort> iWriter, Collection<CohortMeta> cohortMetas) {

    	int count = 0;
        for (CohortMeta cohortMeta : cohortMetas) {
            List<SLCStaffCohortAssociation> staffCohortAssociations = null;
            
            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                staffCohortAssociations = new ArrayList<SLCStaffCohortAssociation>();
            } else {
                staffCohortAssociations = StaffCohortAssociationGenerator.generateLowFi(cohortMeta);
            }
            for (SLCStaffCohortAssociation staffCohort : staffCohortAssociations) {
                iWriter.marshal(staffCohort);
            }
            count += staffCohortAssociations.size();
        }
        return count;
        
    }

    /**
     * Call StaffCohortAssociationGenerator to populates data into 
     * the interchange.
     *
     * @param interchangeObjects
     * @param cohortMetas
     */
    private static int generateStudentCohortAssociation(InterchangeWriter<InterchangeStudentCohort> iWriter, Collection<CohortMeta> cohortMetas) {

    	int count=0;
        for (CohortMeta cohortMeta : cohortMetas) {
            
            List<StudentCohortAssociation> retVal;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                retVal = new ArrayList<StudentCohortAssociation>(0);
            } else {
                count += StudentCohortAssociationGenerator.generateLowFi(iWriter,cohortMeta);
            }
        }
        return count;
    }
    
}
