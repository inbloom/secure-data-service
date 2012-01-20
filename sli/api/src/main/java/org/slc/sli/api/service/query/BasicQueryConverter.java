package org.slc.sli.api.service.query;

import org.apache.avro.Schema;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

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
    EntitySchemaRegistry avroReg;
    
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
                    }
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
        Schema schema = avroReg.findSchemaForName(entityType);
        for (String field : nestedFields) {
            schema = getNestedSchema(schema, field);
        }
        if (schema.getType().equals(Schema.Type.UNION)) {
            for (Schema possibleSchema : schema.getTypes()) {
                if (!possibleSchema.getType().equals(Schema.Type.NULL)) {
                    schema = possibleSchema;
                }
            }
        }
        return schema.getType().toString();
    }
    
    private Schema getNestedSchema(Schema schema, String field) {
        switch (schema.getType()) {
        case NULL:
        case STRING:
        case BYTES:
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
        case MAP:
        case FIXED:
        case ENUM:
            return Schema.create(Schema.Type.NULL);
        case UNION:
            for (Schema possibleSchema : schema.getTypes()) {
                if (!possibleSchema.getType().equals(Schema.Type.NULL)) {
                    schema = possibleSchema;
                    break;
                }
            }
            return getNestedSchema(schema, field);
        case RECORD:
            if (schema.getField(field) != null) {
                return schema.getField(field).schema();
            } else
                return Schema.create(Schema.Type.NULL);
        case ARRAY:
            return getNestedSchema(schema.getElementType(), field);
        default: {
            throw new RuntimeException("Unknown Avro Schema Type: " + schema.getType());
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
            return null;
        else
            return keyAndValue;
    }
    
    private Object convertToType(String type, String value) {
        if (type.equals("INT")) {
            return Integer.parseInt(value);
        } else if (type.equals("BOOLEAN")) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
                return Boolean.parseBoolean(value);
            else
                throw new QueryParseException("");
        } else if (type.equals("STRING")) {
            return value;
        } else if (type.equals("ENUM")) {
            return value;
        } else if (type.equals("LONG")) {
            return Long.parseLong(value);
        } else if (type.equals("FLOAT")) {
            return Float.parseFloat(value);
        } else if (type.equals("DOUBLE")) {
            return Double.parseDouble(value);
        } else
            throw new RuntimeException("Unsupported Avro Schema Type: " + type);
    }
}
