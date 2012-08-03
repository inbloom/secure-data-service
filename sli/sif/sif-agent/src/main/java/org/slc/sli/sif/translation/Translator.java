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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import openadk.library.SIFDataObject;

<<<<<<< HEAD
import org.slc.sli.sif.domain.slientity.SliEntity;
=======
>>>>>>> 163007e9f83d82ad6f81d20ce63882359a7ebd84
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.SliEntity;


@Component
public class Translator<T extends SIFDataObject>
{
    @SuppressWarnings("rawtypes")
    private Map<String, TranslationTask> translationTaskMap;

    @SuppressWarnings("unchecked")
    public List<SliEntity> translate(final T sdo) {
        @SuppressWarnings("rawtypes")
        final TranslationTask task = getTranslationTask(sdo);
        try {
            return task==null ? new ArrayList<SliEntity>() : task.translate(sdo);
        } catch (SifTranslationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public TranslationTask getTranslationTask(final T  sdo) {
        return sdo==null ? null : translationTaskMap.get(sdo.tag());
    }

    @SuppressWarnings("rawtypes")
    public Map<String, TranslationTask> getTranslationTaskMap() {
        return translationTaskMap;
    }

    public void setTranslationTaskMap(@SuppressWarnings("rawtypes") Map<String, TranslationTask> translationTaskMap) {
        this.translationTaskMap = translationTaskMap;
    }

}

