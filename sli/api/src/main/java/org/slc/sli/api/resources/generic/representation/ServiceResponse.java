/*
 *
 *  * Copyright 2012 Shared Learning Collaborative, LLC
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.slc.sli.api.resources.generic.representation;

import org.slc.sli.api.representation.EntityBody;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 8/21/12
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceResponse {
    private List<EntityBody> entityBodyList;
    private long entityCount;

    public ServiceResponse(List<EntityBody> entityBodyList, long entityCount) {
        this.entityBodyList = entityBodyList;
        this.entityCount = entityCount;
    }

    public List<EntityBody> getEntityBodyList() {
        return entityBodyList;
    }

    public void setEntityBodyList(List<EntityBody> entityBodyList) {
        this.entityBodyList = entityBodyList;
    }

    public long getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(long entityCount) {
        this.entityCount = entityCount;
    }
}
