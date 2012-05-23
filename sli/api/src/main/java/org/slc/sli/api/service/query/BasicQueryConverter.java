package org.slc.sli.api.service.query;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Default implementation of the QueryConverter interface
 *
 * Use NeutralQuery or ApiQuery instead.
 *
 * @author dong liu <dliu@wgen.net>
 *
 */
@Deprecated
@Component
public class BasicQueryConverter implements QueryConverter {

    private static String[] reservedQueryKeys = { "start-index", "max-results", "query", "sessionId",
            Resource.FULL_ENTITIES_PARAM, Resource.SORT_BY_PARAM, Resource.SORT_ORDER_PARAM, ParameterConstants.OFFSET,
            ParameterConstants.LIMIT, ParameterConstants.SORT_BY, ParameterConstants.SORT_ORDER,
            ParameterConstants.INCLUDE_FIELDS, ParameterConstants.EXCLUDE_FIELDS };
    private static final Logger LOG = LoggerFactory.getLogger(BasicQueryConverter.class);
    private static final String ENCRYPTION_ERROR = "Unable to perform search operation on PII field ";

    @Autowired
    SchemaRepository schemaRepo;

    @Autowired(required = false)
    EntityEncryption encryptor;

    @Override
    public Query stringToQuery(String entityType, String queryString) {
        return stringToQuery(entityType, queryString, null, null);
    }

    @Override
    public Query stringToQuery(String entityType, String queryString, String sortBy, SortOrder sortOrder) {
        Query mongoQuery = new Query();
        if (queryString == null) {
            queryString = "";
        }
        String[] queryStrings = queryString.split("&");
        try {
            for (String query : queryStrings) {

                if (!isReservedQueryKey(query) && !query.equals("")) {
                    Criteria criteria = null;
                    if (query.contains(">=")) {
                        String[] keyAndValue = getKeyAndValue(query, ">=");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            if (type.isPii()) {
                                throw new SortingException(ENCRYPTION_ERROR + keyAndValue[0]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).gte(
                                    convertToType(type.getType(), keyAndValue[1]));
                        }
                    } else if (query.contains("<=")) {
                        String[] keyAndValue = getKeyAndValue(query, "<=");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            if (type.isPii()) {
                                throw new SortingException(ENCRYPTION_ERROR + keyAndValue[0]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).lte(
                                    convertToType(type.getType(), keyAndValue[1]));
                        }

                    } else if (query.contains("!=")) {
                        String[] keyAndValue = getKeyAndValue(query, "!=");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            Object searchValue = convertToType(type.getType(), keyAndValue[1]);
                            if (type.isPii() && encryptor != null) {
                                searchValue = encryptor.encryptSingleValue(keyAndValue[1]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).ne(searchValue);
                        }
                    } else if (query.contains("=~")) {
                        String[] keyAndValue = getKeyAndValue(query, "=~");
                        ParamType type = findParamType(entityType, keyAndValue[0]);
                        if (type.isPii()) {
                            throw new SortingException(ENCRYPTION_ERROR + keyAndValue[0]);
                        }
                        if (keyAndValue != null) {
                            criteria = Criteria.where("body." + keyAndValue[0]).regex(keyAndValue[1]);
                        }
                    } else if (query.contains("=")) {
                        String[] keyAndValue = getKeyAndValue(query, "=");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            Object searchValue = convertToType(type.getType(), keyAndValue[1]);
                            if (type.isPii() && encryptor != null) {
                                searchValue = encryptor.encryptSingleValue(keyAndValue[1]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).is(searchValue);
                        }

                    } else if (query.contains("<")) {
                        String[] keyAndValue = getKeyAndValue(query, "<");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            if (type.isPii()) {
                                throw new SortingException(ENCRYPTION_ERROR + keyAndValue[0]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).lt(
                                    convertToType(type.getType(), keyAndValue[1]));
                        }

                    } else if (query.contains(">")) {
                        String[] keyAndValue = getKeyAndValue(query, ">");
                        if (keyAndValue != null) {
                            ParamType type = findParamType(entityType, keyAndValue[0]);
                            if (type.isPii()) {
                                throw new SortingException(ENCRYPTION_ERROR + keyAndValue[0]);
                            }
                            criteria = Criteria.where("body." + keyAndValue[0]).gt(
                                    convertToType(type.getType(), keyAndValue[1]));
                        }
                    } else {
                        throw new RuntimeException("Unknown query operation: " + query);
                    }
                    if (criteria != null) {
                        mongoQuery.addCriteria(criteria);
                    }
                }
            }

        } catch (RuntimeException e) {
//            DE260: The log below is possibly a security hole!
//            LOG.error("error parsing query String {} {}", e.getMessage(), queryString);
            throw new QueryParseException(e.getMessage(), queryString);
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            ParamType type = findParamType(entityType, sortBy);
            if (!"NULL".equals(type.getType())) {

                if (type.isPii()) {
                    throw new SortingException("Unable to perform sort operation on PII field " + sortBy);
                }

                if (sortOrder == null) {
                    sortOrder = SortOrder.ascending;
                }
                mongoQuery.sort().on("body." + sortBy, sortOrder.toOrder());
            } else {
                throw new SortingException("Cannot sort by requested field.  Field " + sortBy
                        + " does not exist on entity " + entityType);
            }
        }

        return mongoQuery;
    }

    /**
     * Simple holder class to allow methods to return multiple values.
     *
     * This class is protected access level to support some of the existing unit tests. It is not
     * intended to be used outside this class.
     */
    protected static class ParamType {
        final String type;
        final boolean pii;

        public ParamType(String type, boolean pii) {
            this.type = type;
            this.pii = pii;
        }

        public String getType() {
            return type;
        }

        public boolean isPii() {
            return pii;
        }
    }

    public ParamType findParamType(String entityType, String queryField) {
        String[] nestedFields = queryField.split("\\.");
        NeutralSchema schema = schemaRepo.getSchema(entityType);
        for (String field : nestedFields) {
            schema = getNestedSchema(schema, field);
            if (schema != null) {
                LOG.debug("nested schema type is {}", schema.getSchemaType());
            } else {
                LOG.debug("nested schema type is {}", "NULL");
            }
        }
        if (schema == null) {
            return new ParamType("NULL", false);
        } else if (schema.getSchemaType() == NeutralSchemaType.LIST) {
            for (NeutralSchema possibleSchema : ((ListSchema) schema).getList()) {
                if (possibleSchema.isSimple()) {
                    return new ParamType(possibleSchema.getSchemaType().toString(), isSchemaPii(possibleSchema));
                }
            }
            return new ParamType("LIST", isSchemaPii(schema));
        }
        return new ParamType(schema.getSchemaType().toString(), isSchemaPii(schema));
    }

    private static boolean isSchemaPii(NeutralSchema schema) {
        if (schema == null) {
            return false;
        }
        if (schema.getAppInfo() == null) {
            return false;
        }
        return schema.getAppInfo().isPersonallyIdentifiableInfo();
    }

    private NeutralSchema getNestedSchema(NeutralSchema schema, String field) {
        if (schema == null) {
            return null;
        }
        switch (schema.getSchemaType()) {
        case STRING:
        case INTEGER:
        case DATE:
        case TIME:
        case DATETIME:
        case ID:
        case IDREF:
        case INT:
        case LONG:
        case DOUBLE:
        case BOOLEAN:
        case TOKEN:
            return null;
        case LIST:
            for (NeutralSchema possibleSchema : ((ListSchema) schema).getList()) {
                LOG.debug("possible schema type is {}", possibleSchema.getSchemaType());
                if (getNestedSchema(possibleSchema, field) != null) {
                    return getNestedSchema(possibleSchema, field);
                }
            }
            return null;
        case COMPLEX:
            for (String key : schema.getFields().keySet()) {
                NeutralSchema possibleSchema = schema.getFields().get(key);
                if (key.startsWith("*")) {
                    key = key.substring(1);
                }
                if (key.equals(field)) {
                    return possibleSchema;
                }
            }
            return null;
        default: {
            throw new QueryParseException("Unknown Schema Type: " + schema.getSchemaType(), field);
        }
        }
    }

    private boolean isReservedQueryKey(String queryString) {
        boolean found = false;
        for (String key : reservedQueryKeys) {
            if (queryString.indexOf(key) >= 0) {
                found = true;
            }
        }
        return found;
    }

    private String[] getKeyAndValue(String queryString, String operator) {
        String[] keyAndValue = queryString.split(operator);
        if (keyAndValue.length != 2) {
            throw new RuntimeException();
        } else {
            return keyAndValue;
        }
    }

    private Object convertToType(String type, String value) {
        try {
            if (type.equals("REFERENCE")) {
                return value;
            } else if (type.equals("INT") || type.equals("INTEGER")) {
                return Integer.parseInt(value);
            } else if (type.equals("BOOLEAN")) {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(value);
                } else {
                    throw new RuntimeException("Invalid boolean value");
                }
            } else if (type.equals("STRING") || type.equals("NULL")) {
                return value;
            } else if (type.equals("TOKEN")) {
                return value;
            } else if (type.equals("LONG")) {
                return Long.parseLong(value);
            } else if (type.equals("DOUBLE")) {
                return Double.parseDouble(value);
            } else if (type.equals("DATE")) {
                DatatypeConverter.parseDate(value);
                return value;
            } else if (type.equals("DATETIME")) {
                DatatypeConverter.parseDateTime(value).toString();
                return value;
            } else if (type.equals("TIME")) {
                DatatypeConverter.parseTime(value).toString();
                return value;
            }
            throw new RuntimeException("Unsupported Neutral Schema Type: " + type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
