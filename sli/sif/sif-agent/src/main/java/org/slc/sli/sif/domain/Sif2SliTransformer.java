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

import openadk.library.datamodel.SEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.LEAInfo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.sifentity.SEAInfoEntity;
import org.slc.sli.sif.domain.sifentity.SchoolInfoEntity;
import org.slc.sli.sif.domain.slientity.EntityAdapter;
import org.slc.sli.sif.domain.slientity.GenericEntity;
import org.slc.sli.sif.domain.slientity.SEAEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.domain.sifentity.LEAInfoEntity;
import org.slc.sli.sif.domain.slientity.LEAEntity;

/**
 * Transformer for mapping entities from SIF domain to SLI domain.
 *
 * @author slee
 *
 */
public class Sif2SliTransformer
{
    protected static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Mapper dozerMapper;

    /**
     * Transform an SIF SchoolInfo into a EntityAdapter ready for api client operations.
     *
     * @param genericEntity
     * @param entityType
     * @return EntityAdapter
     */
    public EntityAdapter transform(SchoolInfo schoolInfo) {
        return new EntityAdapter(xform(schoolInfo), "school");
    }

    /**
     * Transform an SIF SchoolInfo into a EntityAdapter ready for api client operations.
     *
     * @param genericEntity
     * @param entityType
     * @return EntityAdapter
     */
    public EntityAdapter transform(LEAInfo info) {
        return new EntityAdapter(xform(info), "educationOrganization");
    }

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(SchoolInfo info) {
        return xform(info).json();
    }

    /**
     * Transform an SIF SchoolInfo into an SLI SchoolEntity.
     *
     * @param SchoolInfo
     * @return SchoolEntity
     */
    public SchoolEntity xform(SchoolInfo schoolInfo) {
        return this.dozerMapper.map(new SchoolInfoEntity(schoolInfo), SchoolEntity.class);
    }

    /**
     * Transform an SIF SchoolInfoEntity into an SLI SchoolEntity.
     *
     * @param SchoolInfoEntity
     * @return SchoolEntity
     */
    public SchoolEntity transform(SchoolInfoEntity schoolInfoEntity) {
        return this.dozerMapper.map(schoolInfoEntity, SchoolEntity.class);
    }

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(LEAInfo leaInfo) {
        return xform(leaInfo).json();
    }

    /**
     * Transform an SIF SchoolInfo into an SLI SchoolEntity.
     *
     * @param SchoolInfo
     * @return SchoolEntity
     */
    public LEAEntity xform(LEAInfo leaInfo) {
        return this.dozerMapper.map(new LEAInfoEntity(leaInfo), LEAEntity.class);
    }

    /**
     * Transform an SIF SchoolInfoEntity into an SLI SchoolEntity.
     *
     * @param SchoolInfoEntity
     * @return SchoolEntity
     */
    public LEAEntity transform(LEAInfoEntity leaInfoEntity) {
        return this.dozerMapper.map(leaInfoEntity, LEAEntity.class);
    }

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(SEAInfo seaInfo) {
        return xform(seaInfo).json();
    }

    /**
     * Transform an SIF SchoolInfo into an SLI SchoolEntity.
     *
     * @param SchoolInfo
     * @return SchoolEntity
     */
    public SEAEntity xform(SEAInfo seaInfo) {
        return this.dozerMapper.map(new SEAInfoEntity(seaInfo), SEAEntity.class);
    }

    /**
     * Transform an SIF SchoolInfoEntity into an SLI SchoolEntity.
     *
     * @param SchoolInfoEntity
     * @return SchoolEntity
     */
    public LEAEntity transform(SEAInfoEntity seaInfoEntity) {
        return this.dozerMapper.map(seaInfoEntity, LEAEntity.class);
    }

}
