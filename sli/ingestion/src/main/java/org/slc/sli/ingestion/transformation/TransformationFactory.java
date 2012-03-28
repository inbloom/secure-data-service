package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
     * Create a transmogrifier based on a jobId and collection names that, when executed, will
     * perform all transformations required for this job.
     *
     * @param collectionNames
     * @param jobId
     * @return
     */
    public Transmogrifier createTransmogrifier(String jobId) {

        List<TransformationStrategy> transformationStrategies = deriveTransformsRequired(defineCollectionsInJob());

        return TransmogrifierImpl.createInstance(jobId, transformationStrategies);
    }

    private List<TransformationStrategy> deriveTransformsRequired(Collection<String> collectionNames) {

        List<TransformationStrategy> transformationStrategies = new ArrayList<TransformationStrategy>();

        for (String strategy : collectionNames) {
            String expectedTransformationStrategy = strategy + getTransformationStrategySuffix();
            LOG.debug("looking up transformation strategy for {}", expectedTransformationStrategy);
            if (applicationContext.containsBeanDefinition(expectedTransformationStrategy)) {
                TransformationStrategy bean = applicationContext.getBean(expectedTransformationStrategy, TransformationStrategy.class);
                LOG.debug("found transformation strategy {}", bean);
                transformationStrategies.add(bean);
            }
        }

        return transformationStrategies;
    }

    private List<String> defineCollectionsInJob() {
        List<String> collectionNames = new ArrayList<String>(neutralRecordMongoAccess.getRecordRepository().getCollectionNames());

        return collectionNames;
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
