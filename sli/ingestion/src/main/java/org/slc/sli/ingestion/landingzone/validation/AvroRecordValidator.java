package org.slc.sli.ingestion.landingzone.validation;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.IngestionAvroEntityValidator;
import org.slc.sli.ingestion.validation.spring.SimpleValidator;
import org.slc.sli.validation.AvroEntityValidator;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

/**
 * Validator for AVRO record schema matches.
 *
 * @author Tom Shewchuk <tshewchuk@wgen.net>
 *
 */
public class AvroRecordValidator extends SimpleValidator<Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(AvroRecordValidator.class);

    @Autowired
    private EntitySchemaRegistry entitySchemaRegistry;

    @Override
    public boolean isValid(Entity entity, ErrorReport errorReport) {
        boolean returnVal = false;
        AvroEntityValidator validator = new IngestionAvroEntityValidator();
        validator.setSchemaRegistry(entitySchemaRegistry);

        // Get the record number, if this is a instance of NeutralRecordEntity.
        String recordNumber = "?";
        if (entity instanceof NeutralRecordEntity) {
            recordNumber = "" + ((NeutralRecordEntity) entity).getRecordNumberInFile();
        }

        // If entity is invalid, log errors.
        try {
            returnVal = validator.validate(entity);

            // Catch validation errors for a defined record type.
        } catch (EntityValidationException ex) {

            // Process each validation error.
            for (ValidationError err : ex.getValidationErrors()) {

                // Get the offending field name.
                String fieldName = err.getFieldName();

                // Formulate error message by error type.
                switch (err.getType()) {
                case REQUIRED_FIELD_MISSING:
                    errorReport.error(getFailureMessage("SL_ERR_MSG14", recordNumber, fieldName),
                            AvroRecordValidator.class);
                    break;
                case UNKNOWN_FIELD:
                    errorReport.error(getFailureMessage("SL_ERR_MSG15", recordNumber, fieldName),
                            AvroRecordValidator.class);
                    break;
                case INVALID_DATE_FORMAT:
                    errorReport.error(getFailureMessage("SL_ERR_MSG16", recordNumber, fieldName),
                            AvroRecordValidator.class);
                    break;
                case ENUMERATION_MISMATCH:
                    String enumValues = Arrays.toString(err.getExpectedTypes());
                    errorReport.error(getFailureMessage("SL_ERR_MSG17", recordNumber, fieldName, enumValues),
                            AvroRecordValidator.class);
                    break;
                case INVALID_DATATYPE:
                    String expectedType = err.getExpectedTypes()[0];
                    errorReport.error(getFailureMessage("SL_ERR_MSG18", recordNumber, fieldName, expectedType),
                            AvroRecordValidator.class);
                    break;
                case REFERENTIAL_INFO_MISSING:
                    errorReport.error(getFailureMessage("SL_ERR_MSG19", recordNumber, fieldName),
                            AvroRecordValidator.class);
                    break;
                default:
                    errorReport.error("Record " + recordNumber + ": " + err.toString() + ".", this);
                    LOG.error("Record " + recordNumber + ": " + err.toString() + ".", this);
                }

            }

        }

        return returnVal;
    }

    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        entitySchemaRegistry = schemaRegistry;
    }
}
