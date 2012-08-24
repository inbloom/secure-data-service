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
package org.slc.sli.api.resources.generic;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.response.DefaultResponseBuilder;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.response.GetAllResponseBuilder;
import org.slc.sli.api.resources.generic.response.GetResponseBuilder;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Base resource class for all dynamic end points
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */

@Scope("request")
@Consumes({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON,
        MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", HypermediaType.VENDOR_SLC_XML + ";charset=utf-8" })
public abstract class GenericResource {

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    protected GetResponseBuilder getResponseBuilder;

    @Autowired
    protected GetAllResponseBuilder getAllResponseBuilder;

    @Autowired
    protected DefaultResponseBuilder defaultResponseBuilder;

    /**
     * @author jstokes
     */
    public static interface ResourceLogic {
        public Response run(Resource resource);
    }

    /**
     * @author jstokes
     */
    public static interface GetResourceLogic {
        public ServiceResponse run(Resource resource);
    }






}
