package org.slc.sli.ingestion.jaxb.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * utilities for jaxb mappings
 *
 * @author dduran
 *
 */
public class MappingUtils {

    public static Unmarshaller createEdFiUnmarshaller() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("org.slc.sli.domain.edfi");
        return jc.createUnmarshaller();
    }

}
