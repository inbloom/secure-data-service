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
import org.slc.sli.ingestion.WorkNote;
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
    public Transmogrifier createTransmogrifier(WorkNote workNote, Job job) {
        Set<String> collectionsToConsider = determineCollectionsToConsider(workNote, job);
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

    private Set<String> determineCollectionsToConsider(WorkNote workNote, Job job) {
        Set<String> collectionsToConsider = null;
        if (workNote.getIngestionStagedEntity().getCollectionNameAsStaged() == null) {

            collectionsToConsider = defineCollectionsInJob(job);
        } else {

            collectionsToConsider = new HashSet<String>();
            collectionsToConsider.add(workNote.getIngestionStagedEntity().getCollectionNameAsStaged());
        }
        return collectionsToConsider;
    }

    private Set<String> defineCollectionsInJob(Job job) {
        return neutralRecordMongoAccess.getRecordRepository().getCollectionNamesForJob(job.getId());
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
