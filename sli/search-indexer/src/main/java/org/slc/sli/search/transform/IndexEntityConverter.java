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
package org.slc.sli.search.transform;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.util.IndexEntityUtil;

/**
 * IndexEntityConverter handles conversion of IndexEntity to and from entity
 *
 */
public class IndexEntityConverter {

    private EntityConverterFactory entityConverterFactory;

    // decrypt records flag
    private boolean decrypt = true;

    public List<IndexEntity> fromEntityJson(String index, String entity) {
        return fromEntityJson(index, Action.INDEX, entity);
    }

    public List<IndexEntity> fromEntityJson(String index, Action action, String entity) {
        Map<String, Object> entityMap = IndexEntityUtil.getEntity(entity);
        return fromEntity(index, action, entityMap);
    }

    @SuppressWarnings("unchecked")
	public List<IndexEntity> fromEntity(String index, Action action, Map<String, Object> entityMap) {
        String type = (String)entityMap.get("type");
        EntityConverter converter = entityConverterFactory.getConverter(type);

        return converter.convert(index, action, entityMap, decrypt);
    }

    public void setDecrypt(boolean decrypt) {
        this.decrypt = decrypt;
    }
    
    public void setEntityConverterFactory(EntityConverterFactory entityConverterFactory) {
        this.entityConverterFactory = entityConverterFactory;
    }

}
