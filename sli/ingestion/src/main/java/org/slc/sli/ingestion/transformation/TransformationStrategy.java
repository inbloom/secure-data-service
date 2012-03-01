package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.handler.Handler;

/**
 * @author ifaybyshev
 *
 * @param <T>
 * @param <O>
 */
public interface TransformationStrategy<T, O> extends Handler<T, O> {
    
}
