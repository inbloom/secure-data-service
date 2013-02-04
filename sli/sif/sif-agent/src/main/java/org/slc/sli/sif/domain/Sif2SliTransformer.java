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

package org.slc.sli.sif.domain;

import java.util.Map;

import openadk.library.datamodel.SEAInfo;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;

/**
 * Transformer for mapping entities from SIF domain to SLI domain.
 *
 * @author slee
 *
 */
public interface Sif2SliTransformer {
    /**
    * Transform an SIF SchoolInfo into a the body of the corresponding SLI
    * entity
    */
    public Map<String, Object> transform(SchoolInfo schoolInfo);

    /**
    * Transform an SIF LEAInfo into a the body of the corresponding SLI entity
    */
    public Map<String, Object> transform(LEAInfo info);

    /**
    * Transform an SIF SEAInfo into a the body of the corresponding SLI entity
    */
    public Map<String, Object> transform(SEAInfo info);

}
