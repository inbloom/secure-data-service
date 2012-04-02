package org.slc.sli.test.utils;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Utilities to help when using JAXB
 *
 * @author dduran
 *
 */
public class JaxbUtils {

    /**
     * Marshal the provided object using System.out
     *
     * @param objectToMarshal
     */
    public static void marshal(Object objectToMarshal) {
        marshal(objectToMarshal, System.out);
    }

    /**
     * Marshal the provided object using the OutputStream specified
     *
     * @param objectToMarshal
     * @param outputStream
     */
    public static void marshal(Object objectToMarshal, OutputStream outputStream) {
        if (objectToMarshal != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(objectToMarshal.getClass());
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
                marshaller.marshal(objectToMarshal, System.out);
            } catch (JAXBException e) {
                System.out.println(e);
            }
        }
    }

    // TODO: right now we will create a new Marshaller every time. We can optimize by caching a
    // Marshaller for every interchange (lazily)
}
