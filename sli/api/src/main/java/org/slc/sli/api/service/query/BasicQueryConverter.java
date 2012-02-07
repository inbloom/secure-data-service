package org.slc.sli.api.service.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Default implementation of the QueryConverter interface
 *
 * @author dong liu <dliu@wgen.net>
 *
 */

@Component
public class BasicQueryConverter implements QueryConverter {

    private static String[] reservedQueryKeys = { "start-index", "max-results", "query", "sessionId" };
    private static final Logger LOG = LoggerFactory.getLogger(BasicQueryConverter.class);

    @Autowired
    SchemaRepository schemaRepo;

    @Override
    public Query stringToQuery(String entityType, String queryString) {
        Query mongoQuery = new Query();
        if (queryString == null)
            queryString = "";
        String[] queryStrings = queryString.split("&");
        try {
            for (String query : queryStrings) {

                if (!isReservedQueryKey(query) && !query.equals("")) {
                    Criteria criteria = null;
                    if (query.contains(">=")) {
                        String[] keyAndValue = getKeyAndValue(query, ">=");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0])
                                    .gte(convertToType(type, keyAndValue[1]));
                        }
                    } else if (query.contains("<=")) {
                        String[] keyAndValue = getKeyAndValue(query, "<=");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0])
                                    .lte(convertToType(type, keyAndValue[1]));
                        }

                    } else if (query.contains("!=")) {
                        String[] keyAndValue = getKeyAndValue(query, "!=");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0]).ne(convertToType(type, keyAndValue[1]));
                        }
                    } else if (query.contains("=")) {
                        String[] keyAndValue = getKeyAndValue(query, "=");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0]).is(convertToType(type, keyAndValue[1]));
                        }

                    } else if (query.contains("<")) {
                        String[] keyAndValue = getKeyAndValue(query, "<");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0]).lt(convertToType(type, keyAndValue[1]));
                        }

                    } else if (query.contains(">")) {
                        String[] keyAndValue = getKeyAndValue(query, ">");
                        if (keyAndValue != null) {
                            String type = findParamType(entityType, keyAndValue[0]);
                            criteria = Criteria.where("body." + keyAndValue[0]).gt(convertToType(type, keyAndValue[1]));
                        }
                    } else
                        throw new RuntimeException();
                    if (criteria != null)
                        mongoQuery.addCriteria(criteria);
                }
            }

        } catch (RuntimeException e) {
            LOG.debug("error parsing query String {}", queryString);
            throw new QueryParseException(queryString);

        }

        return mongoQuery;
    }

    @Override
    public String findParamType(String entityType, String queryField) {
        String[] nestedFields = queryField.split("\\.");
        NeutralSchema schema = schemaRepo.getSchema(entityType);
        for (String field : nestedFields) {
            schema = getNestedSchema(schema, field);
            if (schema != null && schema.getSchemaType() == NeutralSchemaType.COMPLEX) {
                schema = schemaRepo.getSchema(schema.getType());
            }
            if (schema != null) {
                LOG.info("nested schema type is {}", schema.getSchemaType());
            } else
                LOG.info("nested schema type is {}", "NULL");
        }
        if (schema == null) {
            return "NULL";
        } else if (schema.getSchemaType() == NeutralSchemaType.LIST) {
            for (NeutralSchema possibleSchema : ((ListSchema) schema).getList()) {
                if (possibleSchema.isSimple())
                    return possibleSchema.getSchemaType().toString();
            }
            return "LIST";
        }
        return schema.getSchemaType().toString();
    }

    private NeutralSchema getNestedSchema(NeutralSchema schema, String field) {
        if (schema == null)
            return null;
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
                LOG.info("possible schema type is {}", possibleSchema.getSchemaType());
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
            throw new RuntimeException("Unknown Avro Schema Type: " + schema.getSchemaType());
        }
        }
    }

    private boolean isReservedQueryKey(String queryString) {
        boolean found = false;
        for (String key : reservedQueryKeys) {
            if (queryString.indexOf(key) >= 0)
                found = true;
        }
        return found;
    }

    private String[] getKeyAndValue(String queryString, String operator) {
        String[] keyAndValue = queryString.split(operator);
        if (keyAndValue.length != 2)
            throw new RuntimeException();
        else
            return keyAndValue;
    }

    private Object convertToType(String type, String value) {
        if (type.equals("INT") || type.equals("INTEGER")) {
            return Integer.parseInt(value);
        } else if (type.equals("BOOLEAN")) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
                return Boolean.parseBoolean(value);
            else
                throw new QueryParseException("");

            // TODO null type need to be removed after the assessment neutral schema implemented
        } else if (type.equals("STRING") || type.equals("NULL")) {
            return value;
        } else if (type.equals("TOKEN")) {
            return value;
        } else if (type.equals("LONG")) {
            return Long.parseLong(value);
        } else if (type.equals("DOUBLE")) {
            return Double.parseDouble(value);
        } else if (type.equals("DATE")) {
            return javax.xml.bind.DatatypeConverter.parseDate(value);
        } else if (type.equals("DATETIME")) {
            return javax.xml.bind.DatatypeConverter.parseDateTime(value);
        } else if (type.equals("TIME")) {
            return javax.xml.bind.DatatypeConverter.parseTime(value);
        }
        throw new RuntimeException("Unsupported Neutral Schema Type: " + type);
    }
}
