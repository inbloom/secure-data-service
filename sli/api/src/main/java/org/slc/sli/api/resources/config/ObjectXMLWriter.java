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


package org.slc.sli.api.resources.config;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.v1.HypermediaType;


/**
 * Custom Context Resolver that will generate XML for objects
 * other than entities
 *
 * */
@SuppressWarnings("rawtypes")
@Provider
@Component
@Produces({ MediaType.APPLICATION_XML + ";charset=utf-8", HypermediaType.VENDOR_SLC_XML + ";charset=utf-8" })
public class ObjectXMLWriter implements MessageBodyWriter {

    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type.getName().equals("org.slc.sli.api.representation.Home")
                || type.getName().equals("org.slc.sli.api.representation.ErrorResponse")) {
            return true;
        }

        return false;
    }

    @Override
    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.writeValue(entityStream, o);
    }
}
