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

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.stereotype.Component;

/**
 * Custom JAXB Context Resolver that will generate JSON.
 * 
 * */
@SuppressWarnings("rawtypes")
@Provider
@Component
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class JacksonJSONMsgBodyWriter implements MessageBodyWriter {
    
    protected final ObjectMapper om = new ObjectMapper();
    
    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }
    
    @Override
    public long getSize(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }
    
    @Override
    public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        
        Object jsonBody = t;
        
        // check on the class type to see if we need
        // to strip off any extraneous wrapper classes
        if (type != null) {
            
            if (type.getName().equals("org.slc.sli.api.representation.Home")) {
                Home home = (Home) t;
                jsonBody = home.getLinksMap();
            } else if (type.getName().equals("org.slc.sli.api.representation.EntityResponse")) {
                EntityResponse entityReponse = (EntityResponse) t;
                jsonBody = entityReponse.getEntity();
            }
            
        }
        
        om.writeValue(entityStream, jsonBody);
    }
}
