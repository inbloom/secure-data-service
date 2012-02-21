package org.slc.sli.ingestion.jaxb.mapping;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slc.sli.ingestion.jaxb.util.MappingUtils;

/**
 * Mapping EdFi InterchangeAssessmentMetaData to SLI model
 *
 * @author dduran
 *
 */
public class ExampleInterchangeMapping {

    public void testUnmarshal() throws JAXBException, IOException {
        String assessmentInterchangeFile = "edfiXsd/assessment1.xml";
        Unmarshaller u = MappingUtils.createUnmarshallerForPackage("org.slc.sli.ingestion.jaxb.domain.edfi");

        // unmarshal file
        /*
         * InterchangeAssessmentMetadata interchangeAssessmentMetadata =
         * (InterchangeAssessmentMetadata) u
         * .unmarshal(JaxbTest.class.getClassLoader().getResourceAsStream(assessmentInterchangeFile))
         * ;
         *
         * for (org.slc.sli.domain.edfi.ComplexObjectType assessmentComplexObject :
         * interchangeAssessmentMetadata
         * .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor()) {
         *
         * if (assessmentComplexObject instanceof Assessment) {
         *
         * org.slc.sli.domain.sli.Assessment sliAssessment = AssessmentMapping
         * .mapAssessment((Assessment) assessmentComplexObject);
         * System.out.println(SerializationUtils.writeJson(sliAssessment));
         * } else if (assessmentComplexObject instanceof StudentAssessment) {
         *
         * org.slc.sli.domain.sli.StudentAssessmentAssociation sliAssessment =
         * StudentAssessmentAssociationMapping
         * .mapStudentAssessmentAssociation((StudentAssessment) assessmentComplexObject);
         * System.out.println(SerializationUtils.writeJson(sliAssessment));
         * }
         * }
         */
    }
}
