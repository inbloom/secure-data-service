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
     * @throws JAXBException
     */
    public static void marshal(Object objectToMarshal) throws JAXBException {
        marshal(objectToMarshal, System.out);
    }

    /**
     * Marshal the provided object using the OutputStream specified
     *
     * @param objectToMarshal
     * @param outputStream
     * @throws JAXBException
     */
    public static void marshal(Object objectToMarshal, OutputStream outputStream) throws JAXBException {
        if (objectToMarshal != null) {
            long startTime = System.currentTimeMillis();
            
            JAXBContext context = JAXBContext.newInstance(objectToMarshal.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(objectToMarshal, outputStream);

            System.out.println("marshaled " + objectToMarshal.getClass() + " in: "
                    + (System.currentTimeMillis() - startTime));
        } else {
            throw new IllegalArgumentException("Cannot marshal null object");
        }
    }

    // TODO: right now we will create a new Marshaller every time. We can optimize by caching a
    // Marshaller for every interchange (lazily)
}
