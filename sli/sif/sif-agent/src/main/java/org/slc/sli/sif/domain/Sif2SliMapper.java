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

package org.slc.sli.sif.domain;

import openadk.library.student.SchoolInfo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.sifentity.SchoolInfoEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

/**
 * Mapper to map an entity from SIF domain to SLI domain.
 *
 * @author slee
 *
 */
public class Sif2SliMapper
{
    protected static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Mapper dozerMapper;

    /**
     * Map an SIF SchoolInfo into an SLI JsonNode.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode map2json(SchoolInfo schoolInfo) {
        return map(schoolInfo).json();
    }

    /**
     * Map an SIF SchoolInfo into an SLI SchoolEntity.
     *
     * @param SchoolInfo
     * @return SchoolEntity
     */
    public SchoolEntity map(SchoolInfo schoolInfo) {
        return this.dozerMapper.map(new SchoolInfoEntity(schoolInfo), SchoolEntity.class);
    }

    /**
     * Map an SIF SchoolInfoEntity into an SLI SchoolEntity.
     *
     * @param SchoolInfoEntity
     * @return SchoolEntity
     */
    public SchoolEntity map(SchoolInfoEntity schoolInfoEntity) {
        return this.dozerMapper.map(schoolInfoEntity, SchoolEntity.class);
    }

}
