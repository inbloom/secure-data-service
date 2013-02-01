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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import openadk.library.SIFDataObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.sif.domain.slientity.SliEntity;

/**
 * Manages the translation tasks that are run on each SIF data object.
 *
 * @author jtully
 *
 */
@SuppressWarnings("rawtypes")
public class SifTranslationManager {

    private static final Logger LOG = LoggerFactory.getLogger(SifTranslationManager.class);

    private Map<String, List<TranslationTask>> translationMap;

    public void setTranslationMap(Map<String, List<TranslationTask>> translationMap) {
        this.translationMap = translationMap;

    }

    public List<SliEntity> translate(SIFDataObject sifData, String zoneId) {
        List<SliEntity> entities = new ArrayList<SliEntity>();

        // get the list to translation tasks
        List<TranslationTask> translationTasks = translationMap.get(sifData.getObjectType().toString());
        if (translationTasks == null) {
            LOG.error("No TranslationTask found for sif type: " + sifData.getObjectType());
            return entities;
        }

        for (TranslationTask translationTask : translationTasks) {

            try {
                entities.addAll(translationTask.translate(sifData, zoneId));
            } catch (SifTranslationException e) {
                LOG.error("Sif translation exception: ", e);
            }
        }

        return entities;
    }

}
