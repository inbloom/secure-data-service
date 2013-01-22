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

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.v1.HypermediaType;

/**
 * Provides a Jackson context resolver that Jersey uses for serializing to JSON or XML.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 */

@Provider
@Component
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8", HypermediaType.XML + ";charset=utf-8", HypermediaType.VENDOR_SLC_XML + ";charset=utf-8" })
public class CustomJacksonContextResolver implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper = new ObjectMapper();

    public CustomJacksonContextResolver() {
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public ObjectMapper getContext(Class<?> cl) {

        return mapper;

    }
}
