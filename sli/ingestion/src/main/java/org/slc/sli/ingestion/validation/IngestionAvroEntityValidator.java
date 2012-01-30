package org.slc.sli.ingestion.validation;

import java.util.List;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.AvroEntityValidator;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * Extends to add functionality for accepting string versions of primitives and some fuzzy massaging
 * on enum values
 *
 * @author dduran
 *
 */
public class IngestionAvroEntityValidator extends AvroEntityValidator {

   private static final Logger LOG = LoggerFactory.getLogger(IngestionAvroEntityValidator.class);

    @Override
    protected void runValidation(Entity entity, Schema schema) {
        IngestionValidatorInstance vi = new IngestionValidatorInstance();
        boolean valid = vi.matchesSchema(schema, "", entity.getBody(), true);
        if (!valid) {
            LOG.debug("entity is not valid", entity);
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), vi.getErrors());
        }
    }

    /**
     *Validates a single entity
     *@author dduran
     */
    protected static class IngestionValidatorInstance extends ValidatorInstance {

        @Override
        protected boolean matchesSchema(Schema schema, String dataName, Object data, boolean captureErrors) {
            return super.matchesSchema(schema, dataName, data, captureErrors);
        }

        @Override
        protected boolean matchesEnum(Schema enumNum, String dataName, Object dataValue, boolean captureErrors) {
            if (!String.class.isInstance(dataValue)) {
                if (captureErrors) {
                    errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue,
                            new String[] { "String" }));
                }
                return false;
            }
            String dataValueString = prepareEnum(dataValue.toString());
            for (String possibleValue : enumNum.getEnumSymbols()) {
                // TODO remove ignoresCase. Pending all the schemas being updated
                if (possibleValue.equalsIgnoreCase(dataValueString)) {
                    return true;
                }
            }
            if (captureErrors) {
                errors.add(new ValidationError(ErrorType.ENUMERATION_MISMATCH, dataName, dataValueString, enumNum
                        .getEnumSymbols().toArray(new String[0])));
            }
            return false;
        }

        @Override
        protected boolean matchesPrimitive(Schema primitive, String dataName, Object dataValue, boolean captureErrors) {
            switch (primitive.getType()) {
                case STRING:
                    return wrapError(isString(dataValue), captureErrors, dataName, dataValue, "String");
                case BYTES:
                    return wrapError(isByte(dataValue), captureErrors, dataName, dataValue, "Bytes");
                case INT:
                    return wrapError(isInt(dataValue), captureErrors, dataName, dataValue, "32bit Number");
                case LONG:
                    return wrapError(isLong(dataValue), captureErrors, dataName, dataValue, "64bit Number");
                case DOUBLE:
                    return wrapError(isDouble(dataValue), captureErrors, dataName, dataValue,
                            "64bit Floating Point Number");
                case FLOAT:
                    return wrapError(isFloat(dataValue), captureErrors, dataName, dataValue,
                            "32bit Floating Point Number");
                case BOOLEAN:
                    return wrapError(isBoolean(dataValue), captureErrors, dataName, dataValue, "Boolean");
            }
            throw new RuntimeException("Is not a primitive type: " + primitive.getName() + ", " + primitive.getType());
        }

        private boolean isBoolean(Object dataValue) {
            try {
                Boolean.parseBoolean(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        private boolean isFloat(Object dataValue) {
            try {
                Float.parseFloat(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        private boolean isDouble(Object dataValue) {
            try {
                Double.parseDouble(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isByte(Object dataValue) {
            try {
                Byte.parseByte(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        private boolean isLong(Object dataValue) {
            try {
                Long.parseLong(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        private boolean isInt(Object dataValue) {
            try {
                Integer.parseInt(dataValue.toString());
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        private String prepareEnum(String enumVal) {
            enumVal = enumVal.trim().replaceAll("['\"]", "").replaceAll("([\\s,/\\\\.;\\(\\)'\":-])+", "_")
                    .replaceAll("_+$", "");
            if (enumVal.matches("^\\d.*")) {
                enumVal = "TODO_" + enumVal; // we cannot convert these automatically. Someone has
                                             // to
                                             // look at it.
            }
            return enumVal;
        }

        private boolean isString(Object dataValue) {
            return String.class.isInstance(dataValue);
        }

        protected List<ValidationError> getErrors() {
            return super.errors;
        }
    }

}
