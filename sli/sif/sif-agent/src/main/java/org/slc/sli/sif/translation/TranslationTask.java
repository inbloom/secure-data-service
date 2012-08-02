package org.slc.sli.sif.translation;

import openadk.library.SIFDataObject;

import org.slc.sli.sif.domain.slientity.GenericEntity;

/**
 * Interface for translation of all or part of a SIFDataObject
 * to an SLI entity.
 *
 * @author jtully
 *
 */
public interface TranslationTask {
    /*
     *
     * Transform an SIF SchoolInfo into an SLI entity
     */
     public GenericEntity translate(SIFDataObject schoolInfo);
}
