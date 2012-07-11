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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevels;

import org.dozer.DozerConverter;

/**
 * A customized Dozer converter to convert SIF GradeLevels to SLI gradesOffered.
 *
 * @author slee
 *
 */
public class GradeLevelsConverter extends DozerConverter<GradeLevels, List<String>>
{
    public GradeLevelsConverter() {
        super(GradeLevels.class, (Class<List<String>>)new ArrayList<String>().getClass());
    }

    @Override
    public List<String> convertTo(GradeLevels source, List<String> destination)
    {
        if (source==null) {
            return null;
        }
        GradeLevel[] gradeLevels = source.getGradeLevels();
        List<String> list = new ArrayList<String>(gradeLevels.length);
        for (GradeLevel gradeLevel : gradeLevels) {
            list.add(SchoolConverter.toSliGrade(gradeLevel.getCode()));
        }
        return list;
    }

    @Override
    public GradeLevels convertFrom(List<String> source, GradeLevels destination)
    {
        return null;
    }

}

