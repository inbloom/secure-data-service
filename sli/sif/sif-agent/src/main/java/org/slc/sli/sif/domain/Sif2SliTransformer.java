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
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;

import org.codehaus.jackson.JsonNode;
import org.slc.sli.sif.domain.slientity.EntityAdapter;

/**
 * Transformer for mapping entities from SIF domain to SLI domain.
 *
 * @author slee
 *
 */
public interface Sif2SliTransformer
{
    /**
     * Transform an SIF SchoolInfo into a EntityAdapter ready for api client operations.
     *
     * @param genericEntity
     * @param entityType
     * @return EntityAdapter
     */
    public EntityAdapter transform(SchoolInfo schoolInfo);

    /**
     * Transform an SIF LEAInfo into a EntityAdapter ready for api client operations.
     *
     * @param genericEntity
     * @param entityType
     * @return EntityAdapter
     */
    public EntityAdapter transform(LEAInfo info);

    /**
     * Transform an SIF SEAInfo into a EntityAdapter ready for api client operations.
     *
     * @param genericEntity
     * @param entityType
     * @return EntityAdapter
     */
    public EntityAdapter transform(SEAInfo info);

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(SchoolInfo info);

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(LEAInfo leaInfo);

    /**
     * Transform an SIF SchoolInfo into a corresponding SLI JsonNode ready for operations.
     *
     * @param SchoolInfo
     * @return JsonNode
     */
    public JsonNode transform2json(SEAInfo seaInfo);

}
