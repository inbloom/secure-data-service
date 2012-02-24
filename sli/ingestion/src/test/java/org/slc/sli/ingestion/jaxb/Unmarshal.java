package org.slc.sli.ingestion.jaxb;

import java.io.File;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import org.slc.sli.ingestion.jaxb.domain.edfi.Assessment;
import org.slc.sli.ingestion.jaxb.domain.edfi.ComplexObjectType;
import org.slc.sli.ingestion.jaxb.domain.edfi.InterchangeAssessmentMetadata;
import org.slc.sli.ingestion.jaxb.util.MappingUtils;

/**
 *
 * @author dduran
 *
 */
public class Unmarshal {
    @Test
    public void testUnmarshal() throws JAXBException {
        Unmarshaller unmarshaller = MappingUtils.createUnmarshallerForPackage("org.slc.sli.ingestion.jaxb.domain.edfi");
        unmarshaller.setValidating(true);

        long timeNow = System.currentTimeMillis();
        Object edFiObject = unmarshaller.unmarshal(new File("/Users/dduran/assessment1.xml"));
        System.out.println("jaxb processing time: " + (System.currentTimeMillis() - timeNow));

        InterchangeAssessmentMetadata interchangeAssessmentMetadata = (InterchangeAssessmentMetadata) edFiObject;

        for (ComplexObjectType assessmentComplexObject : interchangeAssessmentMetadata
                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor()) {

            if (assessmentComplexObject instanceof Assessment) {
                Assessment assessment = (Assessment) assessmentComplexObject;
                System.out.println(assessment.getMaxRawScore());
                System.out.println(assessment.getAssessmentTitle());
            }
        }
    }
}
