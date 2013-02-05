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

package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * @author ifaybyshev
 *
 *         Factory for transformation strategies
 *
 */
public class TransformationFactory implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(TransformationFactory.class);

    private ApplicationContext applicationContext;

    private String transformationStrategySuffix;

    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    /**
     * Create a transmogrifier that contains all the transformations the WorkNote requires.
     *
     * @param workNote
     * @param job
     * @return
     */
    public Transmogrifier createTransmogrifier(RangedWorkNote workNote, Job job) {
        Set<String> collectionsToConsider = determineCollectionsToConsider(workNote);
        List<TransformationStrategy> transformationStrategies = deriveTransformsRequired(collectionsToConsider);
        return TransmogrifierImpl.createInstance(job, transformationStrategies, workNote);
    }

    private List<TransformationStrategy> deriveTransformsRequired(Collection<String> collectionNames) {
        List<TransformationStrategy> transformationStrategies = new ArrayList<TransformationStrategy>();

        for (String strategy : collectionNames) {
            String expectedTransformationStrategy = strategy + getTransformationStrategySuffix();
            LOG.debug("looking up transformation strategy for {}", expectedTransformationStrategy);
            if (applicationContext.containsBeanDefinition(expectedTransformationStrategy)) {
                TransformationStrategy bean = applicationContext.getBean(expectedTransformationStrategy,
                        TransformationStrategy.class);
                LOG.debug("found transformation strategy {}", bean);
                transformationStrategies.add(bean);
            }
        }

        return transformationStrategies;
    }

    private Set<String> determineCollectionsToConsider(RangedWorkNote workNote) {
        Set<String> collectionsToConsider = null;
        if (workNote.getIngestionStagedEntity().getCollectionNameAsStaged() == null) {

            collectionsToConsider = defineCollectionsInJob();
        } else {

            collectionsToConsider = new HashSet<String>();
            collectionsToConsider.add(workNote.getIngestionStagedEntity().getCollectionNameAsStaged());
        }
        return collectionsToConsider;
    }

    private Set<String> defineCollectionsInJob() {
        return neutralRecordMongoAccess.getRecordRepository().getStagedCollectionsForJob();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getTransformationStrategySuffix() {
        return transformationStrategySuffix;
    }

    public void setTransformationStrategySuffix(String transformationStrategySuffix) {
        this.transformationStrategySuffix = transformationStrategySuffix;
    }

    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

}
