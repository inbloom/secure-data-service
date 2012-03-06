package org.slc.sli.ingestion.transformation;

/**
 * @author dduran
 */
public interface TransformationStrategy {

    /**
     * Transformation of data
     */
    void transform();

    /**
     * Loading data into local storage
     */
    void loadData();

    /**
     * Persisting transformed data (either file or database)
     *
     * @return String status
     */
    String persist();

}
