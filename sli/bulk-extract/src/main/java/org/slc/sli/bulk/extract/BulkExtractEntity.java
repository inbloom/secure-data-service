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
package org.slc.sli.bulk.extract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;

/**
 * @author tke
 *
 */
public class BulkExtractEntity implements Entity{

    private final static String TYPE="bulkExtractEntity";

    private Map<String, Object> body = new HashMap<String, Object>();

    private final Map<String, Object> metaData = new HashMap<String, Object>();

    private String entityId;

    public BulkExtractEntity(Map<String, Object> body, String id){
        this.body = body;
        this.entityId = id;
    }

    @Override
    public Map<String, Object> getBody() {
        return body;
    }


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String id){
        this.entityId = id;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.domain.Entity#getMetaData()
     */
    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.domain.Entity#getStagedEntityId()
     */
    @Override
    public String getStagedEntityId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CalculatedData<String> getCalculatedValues() {
        return new CalculatedData<String>();
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregates() {
        return new CalculatedData<Map<String, Integer>>();
    }

    @Override
    public Map<String, List<Entity>> getEmbeddedData() {
        return new HashMap<String, List<Entity>>();
    }

    @Override
    public Map<String, List<Map<String, Object>>> getDenormalizedData() {
        return new HashMap<String, List<Map<String, Object>>>();
    }


}
