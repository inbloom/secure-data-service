package org.slc.sli.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates an Entity body against an Avro schema.
 *
 * @author Sean Melody <smelody@wgen.net>
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Component
@Deprecated
public class AvroEntityValidator implements EntityValidator {
    private static final Logger LOG = LoggerFactory.getLogger(AvroEntityValidator.class);

    @Autowired
    private EntitySchemaRegistry entitySchemaRegistry;

    /**
     * Validates the given entity using its Avro schema.
     */
    public boolean validate(Entity entity) throws EntityValidationException {
        LOG.debug("validating entity {}", entity.getBody());
        Schema schema = entitySchemaRegistry.findSchemaForType(entity);
        if (schema == null) {
            throw new RuntimeException("No schema associated for type: " + entity.getType());
        }

        runValidation(entity, schema);

        return true;
    }

    protected void runValidation(Entity entity, Schema schema) {
        ValidatorInstance vi = new ValidatorInstance();
        boolean valid = vi.matchesSchema(schema, "", entity.getBody(), true);
        if (!valid) {
            LOG.debug("entity is not valid", entity);
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), vi.errors);
        }
    }

    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.entitySchemaRegistry = schemaRegistry;
    }

    /**
     * Validates a single entity. Not thread safe or reusable.
     *
     * @author Ryan Farris <rfarris@wgen.net>
     *
     */
    protected static class ValidatorInstance {

        final protected List<ValidationError> errors = new LinkedList<ValidationError>();

        private boolean matchesNull(String dataName, Object dataValue, boolean captureErrors) {
            boolean isNull = dataValue == null;
            if (captureErrors) {
                errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue, null));
            }
            return isNull;
        }

        private boolean matchesField(Schema.Field field, String dataName, Object dataValue, boolean captureErrors) {
            return matchesSchema(field.schema(), dataName, dataValue, captureErrors);
        }

        @SuppressWarnings("unchecked")
        private boolean matchesArray(Schema array, String dataName, Object dataValue, boolean captureErrors) {
            if (!List.class.isInstance(dataValue)) {
                if (captureErrors) {
                    errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue,
                            new String[] { "Array" }));
                }
                return false;
            }
            boolean allMatch = true;
            int index = 0;
            for (Object arrayValue : (List<Object>) dataValue) {
                allMatch = allMatch
                        & matchesSchema(array.getElementType(), buildName(dataName, index), arrayValue, captureErrors);
                index++;
            }
            return allMatch;
        }

        private boolean matchesMap(Schema map, String dataName, Object dataValue, boolean captureErrors) {
//            throw new UnsupportedOperationException("Map value validation not implemented");
        	return true; //XXX - hack.  Fix later
        }

        private boolean matchesFixed(Schema fixed, String dataName, Object dataValue, boolean captureErrors) {
            throw new UnsupportedOperationException("Fixed value validation not implemented");
        }

        protected boolean matchesEnum(Schema enumNum, String dataName, Object dataValue, boolean captureErrors) {
            if (!String.class.isInstance(dataValue)) {
                if (captureErrors) {
                    errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue,
                            new String[] { "String" }));
                }
                return false;
            }

            for (String possibleValue : enumNum.getEnumSymbols()) {
                // TODO remove ignoresCase. Pending all the schemas being updated
                if (possibleValue.equalsIgnoreCase(dataValue.toString())) {
                    return true;
                }
            }
            if (captureErrors) {
                errors.add(new ValidationError(ErrorType.ENUMERATION_MISMATCH, dataName, dataValue, enumNum
                        .getEnumSymbols().toArray(new String[0])));
            }
            return false;
        }

        private boolean matchesRecord(Schema record, String dataName, Object dataValue, boolean captureErrors) {
            if ("java.util.Calendar".equals(record.getFullName())) {
                if (String.class.isInstance(dataValue)) {
                    try {
                        javax.xml.bind.DatatypeConverter.parseDateTime((String) dataValue);
                        return true;
                    } catch (IllegalArgumentException e) {
                        try {
                            javax.xml.bind.DatatypeConverter.parseDate((String) dataValue);
                            return true;
                        } catch (IllegalArgumentException e2) {
                            if (captureErrors) {
                                errors.add(new ValidationError(ErrorType.INVALID_DATE_FORMAT, dataName, dataValue,
                                        new String[] { "RFC 3339" }));
                            }
                            return false;
                        }
                    }
                }
            }

            if (!Map.class.isInstance(dataValue)) {
                if (captureErrors) {
                    errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue,
                            new String[] { "Map" }));
                }
                return false;
            }

            boolean matchesAllFields = true;
            Set<String> fieldsSeen = new HashSet<String>();

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) dataValue;
            for (Schema.Field field : record.getFields()) {
                Object fieldDataValue = map.get(field.name());
                if (!matchesField(field, buildName(dataName, field.name()), fieldDataValue, captureErrors)) {
                    matchesAllFields = false;
                }
                fieldsSeen.add(field.name());
            }

            Set<String> dataFields = new HashSet<String>(map.keySet());
            dataFields.removeAll(fieldsSeen);
            if (dataFields.size() > 0) {
                for (String notSeen : dataFields) {
                    errors.add(new ValidationError(ErrorType.UNKNOWN_FIELD, buildName(dataName, notSeen), map
                            .get(notSeen), new String[0]));
                }
            }
            return matchesAllFields && dataFields.size() == 0;
        }

        protected boolean matchesPrimitive(Schema primitive, String dataName, Object dataValue, boolean captureErrors) {
            switch (primitive.getType()) {
                case STRING:
                    return wrapError(String.class.isInstance(dataValue), captureErrors, dataName, dataValue, "String");
                case BYTES:
                    return wrapError(byte[].class.isInstance(dataValue), captureErrors, dataName, dataValue, "Bytes");
                case INT:
                    return wrapError(Integer.class.isInstance(dataValue), captureErrors, dataName, dataValue,
                            "32bit Number");
                case LONG:
                    return wrapError(Long.class.isInstance(dataValue), captureErrors, dataName, dataValue,
                            "64bit Number");
                case DOUBLE:
                    return wrapError(Double.class.isInstance(dataValue), captureErrors, dataName, dataValue,
                            "64bit Floating Point Number");
                case FLOAT:
                    return wrapError(Float.class.isInstance(dataValue), captureErrors, dataName, dataValue,
                            "32bit Floating Point Number");
                case BOOLEAN:
                    return wrapError(Boolean.class.isInstance(dataValue), captureErrors, dataName, dataValue, "Boolean");
            }
            throw new RuntimeException("Is not a primitive type: " + primitive.getName() + ", " + primitive.getType());
        }

        private boolean matchesUnion(Schema union, String dataName, Object dataValue, boolean captureErrors) {
            boolean nullable = false;
            if (union.getTypes().size() == 2 && containsNull(union.getTypes())) {
                // The usual case is that the union has two possibilities: null and one other type.
                // If this is the case, we can capture errors when we check the provided data
                // against the type.
                for (Schema possibleSchema : union.getTypes()) {
                    if (Schema.Type.NULL.equals(possibleSchema.getType())) {
                        nullable = true;
                        if (matchesSchema(possibleSchema, dataName, dataValue, false)) {
                            return true;
                        }
                    } else {
                        if (matchesSchema(possibleSchema, dataName, dataValue, true)) {
                            return true;
                        }
                    }
                }
                if (captureErrors) {
                    String[] validTypes = new String[union.getTypes().size()];
                    for (int i = 0; i < union.getTypes().size(); i++) {
                        validTypes[i] = union.getTypes().get(i).getName();
                    }
                    if (!nullable) {
                        errors.add(new ValidationError(ErrorType.REQUIRED_FIELD_MISSING, dataName, dataValue,
                                validTypes));
                    }
                }
            } else {
                // If the union has multiple types, we cannot be sure what the input was supposed to
                // be, so we turn off error capture for the matchesSchema call.
                for (Schema possibleSchema : union.getTypes()) {
                    if (possibleSchema.getType().equals(Schema.Type.NULL)) {
                        nullable = true;
                    }
                    if (matchesSchema(possibleSchema, dataName, dataValue, false)) {
                        return true;
                    }
                }
                if (captureErrors) {
                    String[] validTypes = new String[union.getTypes().size()];
                    for (int i = 0; i < union.getTypes().size(); i++) {
                        validTypes[i] = union.getTypes().get(i).getName();
                    }
                    if (!nullable) {
                        errors.add(new ValidationError(ErrorType.REQUIRED_FIELD_MISSING, dataName, dataValue,
                                validTypes));
                    } else {
                        errors.add(new ValidationError(ErrorType.INVALID_DATATYPE, dataName, dataValue, validTypes));
                    }
                }
            }

            return false;
        }

        protected boolean matchesSchema(Schema schema, String dataName, Object data, boolean captureErrors) {
            switch (schema.getType()) {
                case NULL:
                    return matchesNull(dataName, data, captureErrors);
                case UNION:
                    return matchesUnion(schema, dataName, data, captureErrors);
                case STRING:
                case BYTES:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                case BOOLEAN:
                    return matchesPrimitive(schema, dataName, data, captureErrors);
                case RECORD:
                    return matchesRecord(schema, dataName, data, captureErrors);
                case ENUM:
                    return matchesEnum(schema, dataName, data, captureErrors);
                case ARRAY:
                    return matchesArray(schema, dataName, data, captureErrors);
                case MAP:
                    return matchesMap(schema, dataName, data, captureErrors);
                case FIXED:
                    return matchesFixed(schema, dataName, data, captureErrors);
                default: {
                    throw new RuntimeException("Unknown Avro Schema Type: " + schema.getType());
                }
            }
        }

        private static boolean containsNull(List<Schema> schemas) {
            for (Schema s : schemas) {
                if (Schema.Type.NULL.equals(s.getType())) {
                    return true;
                }
            }
            return false;
        }

        protected boolean wrapError(boolean isMatch, boolean captureErrors, String dataName, Object dataValue,
                String expectedType) {
            if (!isMatch && captureErrors) {
                ErrorType type = dataValue == null ? ErrorType.REQUIRED_FIELD_MISSING : ErrorType.INVALID_DATATYPE;
                errors.add(new ValidationError(type, dataName, dataValue, new String[] { expectedType }));
            }
            return isMatch;
        }

        private static String buildName(String parent, String name) {
            if (!parent.equals("")) {
                return parent + "." + name;
            } else {
                return name;
            }
        }

        private static String buildName(String parent, int index) {
            return parent + "[" + index + "]";
        }
    }

}
