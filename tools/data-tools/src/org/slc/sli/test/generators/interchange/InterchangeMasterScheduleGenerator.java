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

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.SLCCourseOffering;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.SLCSection;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CourseOfferingGenerator;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Master Schedule Interchange as derived from the variable:
 * - sectionMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeMasterScheduleGenerator {

    /**
     * Sets up a new Master Schedule Interchange and populates it
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeMasterSchedule> iWriter) {

//        InterchangeMasterSchedule interchange = new InterchangeMasterSchedule();
//        List<ComplexObjectType> interchangeObjects = interchange.getCourseOfferingOrSectionOrBellSchedule();

        writeEntitiesToInterchange(iWriter);

//        return interchange;
    }

    /**
     * Generates the individual entities that can generate a Master Schedule
     *
     * @param interchangeObjects
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeMasterSchedule> iWriter) {

        generateCourseOffering(iWriter, MetaRelations.COURSEOFFERING_MAP.values());
        generateSections(iWriter, MetaRelations.SECTION_MAP.values());

    }

    private static void generateCourseOffering(InterchangeWriter<InterchangeMasterSchedule> iWriter,
            Collection<CourseOfferingMeta> courseOfferingMetas) {
        long startTime = System.currentTimeMillis();

        for (CourseOfferingMeta courseOfferingMeta : courseOfferingMetas) {
            SLCCourseOffering courseOffering;
            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                courseOffering = CourseOfferingGenerator.generate(courseOfferingMeta);
            } else {
                courseOffering = CourseOfferingGenerator.generateLowFi(courseOfferingMeta);
            }
//            interchangeObjects.add(courseOffering);
            iWriter.marshal(courseOffering);
        }
        System.out.println("Generated " + courseOfferingMetas.size() + " CourseOfferings in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all sections and, using an Section Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param sectionMetas
     */
    private static void generateSections(InterchangeWriter<InterchangeMasterSchedule> iWriter,
            Collection<SectionMeta> sectionMetas) {
        long startTime = System.currentTimeMillis();

        for (SectionMeta sectionMeta : sectionMetas) {

            SLCSection section;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                section = null;
            } else {
            	section = SectionGenerator.generateMediumFi(sectionMeta);
            }

//            interchangeObjects.add(section);
            iWriter.marshal(section);
        }

        System.out.println("generated " + sectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
