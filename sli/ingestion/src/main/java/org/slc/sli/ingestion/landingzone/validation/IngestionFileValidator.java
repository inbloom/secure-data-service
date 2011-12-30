package org.slc.sli.ingestion.landingzone.validation;

import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.validation.spring.SimpleValidator;

/**
 * Base Ingestion File validator.
 *
 * @author okrook
 *
 */
public abstract class IngestionFileValidator extends SimpleValidator<FileEntryDescriptor> {
}
