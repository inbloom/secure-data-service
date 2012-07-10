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

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.sifentity.SchoolInfoEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

public class Sif2SliMapper
{
    @Autowired
    private Mapper dozerMapper;

    public SchoolEntity map(SchoolInfoEntity schoolInfoEntity) {
        return this.dozerMapper.map(schoolInfoEntity, SchoolEntity.class);
    }


}
