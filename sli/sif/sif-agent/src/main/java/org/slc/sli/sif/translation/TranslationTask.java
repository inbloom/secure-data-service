package org.slc.sli.sif.translation;

import openadk.library.SIFDataObject;

import org.slc.sli.sif.domain.slientity.GenericEntity;

/**
 * Interface for translation of all or part of a SIFDataObject
 * to an SLI entity.
 *
 * @author jtully
 */
public interface TranslationTask<A extends SIFDataObject, B extends GenericEntity> {

    /**
     * Transform a SIF SifDataObject into an SLI entity
     * @param T
     */
     public B translate(A sifObject);
}

