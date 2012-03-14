package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ifaybyshev
 *
 *         Factory for transformation strategies
 *
 */
public class TransformationFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private String transformationStrategySuffix;

    /**
     * Create a transmogrifier based on a jobId and collection names that, when executed, will
     * perform all transformations required for this job.
     *
     * @param collectionNames
     * @param jobId
     * @return
     */
    public Transmogrifier createTransmogrifier(Collection<String> collectionNames, String jobId) {

        List<TransformationStrategy> transformationStrategies = deriveTransformsRequired(collectionNames);

        return TransmogrifierImpl.createInstance(jobId, transformationStrategies);
    }

    private List<TransformationStrategy> deriveTransformsRequired(Collection<String> collectionNames) {

        List<TransformationStrategy> transformationStrategies = new ArrayList<TransformationStrategy>();

        for (String strategy : collectionNames) {
            String expectedTransformationStrategy = strategy + getTransformationStrategySuffix();
            if (applicationContext.containsBeanDefinition(expectedTransformationStrategy)) {
                transformationStrategies.add(applicationContext.getBean(expectedTransformationStrategy, TransformationStrategy.class));
            }
        }

        return transformationStrategies;
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
}
