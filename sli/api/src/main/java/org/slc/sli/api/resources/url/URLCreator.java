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


package org.slc.sli.api.resources.url;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Handles creating different types of URL sets.
 *
 * @author srupasinghe
 *
 */
public abstract class URLCreator {
    
    @Autowired
    protected EntityDefinitionStore store;

    @Autowired
    protected Repository<Entity> repo;

    /**
     * Returns a list of embedded Urls that matches the given parameters
     *
     * @param uriInfo
     * @param params
     * @return
     */
    public abstract List<EmbeddedLink> getUrls(final UriInfo uriInfo, String id, String type, NeutralQuery neutralQuery);

    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }

    public void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }
}
