package org.slc.sli.ingestion.transformation;

/**
 * Base TransformationStrategy.
 *
 * @author dduran
 *
 */
public abstract class AbstractTransformationStrategy implements TransformationStrategy {

    protected String batchJobId;

    abstract void loadData();

    abstract void transform();

    abstract void persist();

    @Override
    public void perform(String batchJobId) {

        this.batchJobId = batchJobId;

        loadData();

        transform();

        persist();
    }

}
