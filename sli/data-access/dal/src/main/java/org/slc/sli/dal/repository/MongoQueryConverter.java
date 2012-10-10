/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 *
 * @author Dong Liu dliu@wgen.net
 * @author kmyers
 */
@Component
public class MongoQueryConverter {
    private static final String MONGO_ID = "_id";
    private static final String MONGO_BODY = "body.";
    private static final String ENCRYPTION_ERROR = "Unable to perform search operation on PII field ";

    private static final Logger LOG = LoggerFactory.getLogger(MongoQueryConverter.class);

    @Autowired
    private IdConverter idConverter;

    @Autowired(required = false)
    @Qualifier("entityEncryption")
    private EntityEncryption encryptor;

    @Autowired
    private SchemaRepository schemaRepo;

    /**
     * Each operator (>, <, !=, etc) mapped to how to create a Mongo criteria for it
     *
     *
     * @author kmyers
     *
     */
    protected interface MongoCriteriaGenerator {
        public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria);
    }

    private Map<String, MongoCriteriaGenerator> operatorImplementations;


    @SuppressWarnings("unchecked")
    private List<Object> convertIds(Object rawValues) {
        List<String> idList = null;

        //type checking
        if (rawValues instanceof List<?>) {
            try {
                idList = (List<String>) rawValues;
            } catch (ClassCastException cce) {
                throw new RuntimeException("IDs must be List<String>");
            }

        } else if (rawValues instanceof String) {
            String ids = (String) rawValues;
            idList = Arrays.asList(ids.split(","));
        }

        if (idList == null) {
            throw new QueryParseException("Invalid paramater for IDs", (rawValues == null) ? "NULL" : rawValues.toString());
        }

        //conversion
        List<Object> databaseIds = new ArrayList<Object>();

        for (String id : idList) {
            Object databaseId = idConverter.toDatabaseId(id);
            if (databaseId == null) {
                LOG.debug("Unable to process id {}", new Object[] { id });
            }
            databaseIds.add(databaseId);
        }

        return databaseIds;
    }

    public MongoQueryConverter() {

        this.operatorImplementations = new HashMap<String, MongoCriteriaGenerator>();

        // =
        this.operatorImplementations.put("=", new MongoCriteriaGenerator() {
            @Override
            @SuppressWarnings("unchecked")
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                Object value = neutralCriteria.getValue();
                if (neutralCriteria.getKey().equals(MONGO_ID)) {
                    return Criteria.where(MONGO_ID).in(convertIds(value));
                } else if (value instanceof List) {
                    return Criteria.where(prefixKey(neutralCriteria)).in((List<Object>) neutralCriteria.getValue());
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).is(neutralCriteria.getValue());
                }
            }
        });

        // =
        this.operatorImplementations.put("in", new MongoCriteriaGenerator() {
            @Override
            @SuppressWarnings("unchecked")
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (neutralCriteria.getKey().equals(MONGO_ID)) {
                    return Criteria.where(MONGO_ID).in(convertIds(neutralCriteria.getValue()));
                } else if (criteria != null) {
                    criteria.in((List<Object>) neutralCriteria.getValue());
                    return criteria;
                } else {
                     try {
                        return Criteria.where(prefixKey(neutralCriteria)).in((List<Object>) neutralCriteria.getValue());
                     } catch (ClassCastException cce) {
                        throw new QueryParseException("Invalid list of in values " + neutralCriteria.getValue(), neutralCriteria.toString());
                     }
                 }
             }
        });

        this.operatorImplementations.put("nin", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (neutralCriteria.getKey().equals(MONGO_ID)) {
                    return Criteria.where(MONGO_ID).in(convertIds(neutralCriteria.getValue()));
                } else if (criteria != null) {
                    criteria.nin(neutralCriteria.getValue());
                    return criteria;
                } else {
                    try {
                        return Criteria.where(prefixKey(neutralCriteria)).nin(neutralCriteria.getValue());
                    } catch (ClassCastException cce) {
                        throw new QueryParseException("Invalid list of in values " + neutralCriteria.getValue(), neutralCriteria.toString());
                    }
                }
            }
        });

        // >=
        this.operatorImplementations.put(">=", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.gte(neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).gte(neutralCriteria.getValue());
                }
            }
        });

        // <=
        this.operatorImplementations.put("<=", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.lte(neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).lte(neutralCriteria.getValue());
                }
            }
        });

        // !=
        this.operatorImplementations.put("!=", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.ne(neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).ne(neutralCriteria.getValue());
                }
            }
        });

        // =~
        this.operatorImplementations.put("=~", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.regex((String) neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).regex((String) neutralCriteria.getValue());
                }
            }
        });

        // <
        this.operatorImplementations.put("<", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.lt(neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).lt(neutralCriteria.getValue());
                }
            }
        });

        // >
        this.operatorImplementations.put(">", new MongoCriteriaGenerator() {
            @Override
            public Criteria generateCriteria(NeutralCriteria neutralCriteria, Criteria criteria) {
                if (criteria != null) {
                    criteria.gt(neutralCriteria.getValue());

                    return criteria;
                } else {
                    return Criteria.where(prefixKey(neutralCriteria)).gt(neutralCriteria.getValue());
                }
            }
        });

    }

    /**
     * Returns the provided string with body. appended if the key is not "_id" and the criteria allows a prefix.
     *
     *
     * @param neutralCriteria
     * @return
     */
    protected static String prefixKey(NeutralCriteria neutralCriteria) {
        String key = neutralCriteria.getKey();

        if (key.equals(MONGO_ID)) {
            return key;
        } else if (neutralCriteria.canBePrefixed()) {
            return MONGO_BODY + key;
        } else {
            return key;
        }
    }

    /**
     * Converts a given neutral query into a mongo Query object where each argument has
     * been converted into the proper type.
     *
     *
     *
     * @param entityName name of collection being queried against
     * @param neutralQuery database independent representation of query to be read
     * @return a mongo specific database query implementation of the neutral query
     */
    public Query convert(String entityName, NeutralQuery neutralQuery) {
        Query mongoQuery = new Query();

        if (neutralQuery != null) {
            // Include fields
            if (neutralQuery.getIncludeFields() != null) {
                for (String includeField : neutralQuery.getIncludeFields()) {
                    mongoQuery.fields().include(MONGO_BODY + includeField);
                }
                mongoQuery.fields().include("type");
                mongoQuery.fields().include("metaData");
            } else if (neutralQuery.getExcludeFields() != null) {
                for (String excludeField : neutralQuery.getExcludeFields()) {
                    mongoQuery.fields().exclude(MONGO_BODY + excludeField);
                }
            }

            // offset
            if (neutralQuery.getOffset() > 0) {
                mongoQuery.skip(neutralQuery.getOffset());
            }

            // limit
            if (neutralQuery.getLimit() > 0) {
                mongoQuery.limit(neutralQuery.getLimit());
            }

            NeutralSchema entitySchema = this.schemaRepo.getSchema(entityName);

            // sorting
            if (neutralQuery.getSortBy() != null) {
                if (neutralQuery.getSortOrder() != null) {
                    Order sortOrder = neutralQuery.getSortOrder().equals(NeutralQuery.SortOrder.ascending) ? Order.ASCENDING
                            : Order.DESCENDING;
                    mongoQuery.sort().on(MONGO_BODY + neutralQuery.getSortBy(), sortOrder);
                } else { // default to ascending order
                    mongoQuery.sort().on(MONGO_BODY + neutralQuery.getSortBy(), Order.ASCENDING);
                }

                NeutralSchema fieldSchema = this.getFieldSchema(entitySchema, neutralQuery.getSortBy());
                if (fieldSchema != null && fieldSchema.isPii()) {
                    throw new QueryParseException(ENCRYPTION_ERROR + " cannot be sorted on", neutralQuery.toString());
                }
            }

            Criteria criteria = convertToCriteria(entityName, neutralQuery, entitySchema);
            mongoQuery.addCriteria(criteria);
        }

        return mongoQuery;
    }


    /**
     * Converts a given neutral sub-query into a mongo Criteria object where each argument has
     * been converted into the proper type.
     */
    public Criteria convertToCriteria(String entityName, NeutralQuery neutralQuery, NeutralSchema entitySchema) {
        Map<String, List<NeutralCriteria>> fields = new HashMap<String, List<NeutralCriteria>>();
        
        // other criteria
        for (NeutralCriteria neutralCriteria : neutralQuery.getCriteria()) {
            String key = neutralCriteria.getKey();
            String operator = neutralCriteria.getOperator();
            Object value = neutralCriteria.getValue();
            Object originalValue = value;
            NeutralSchema fieldSchema = this.getFieldSchema(entitySchema, key);

            if (fieldSchema != null) {
                value = fieldSchema.convert(neutralCriteria.getValue());
                if (fieldSchema.isPii()) {
                    if (operator.contains("<") || operator.contains(">") || operator.contains("~")) {
                        throw new QueryParseException(ENCRYPTION_ERROR + value, neutralQuery.toString());
                    }

                    if (encryptor != null) {
                        value = encryptor.encryptSingleValue(value);
                    }
                }
            }

            neutralCriteria.setValue(value);
            NeutralCriteria criteriaToAdd = new NeutralCriteria(neutralCriteria.getKey(),
                    neutralCriteria.getOperator(), neutralCriteria.getValue(), neutralCriteria.canBePrefixed());
            if (!fields.containsKey(criteriaToAdd.getKey())) {
                List<NeutralCriteria> list = new ArrayList<NeutralCriteria>();
                fields.put(criteriaToAdd.getKey(), list);
            }
            //add the criteria to the map
            fields.get(neutralCriteria.getKey()).add(criteriaToAdd);
            //set the original value back
            neutralCriteria.setValue(originalValue);
        }

        // merge the criteria into one
        Criteria mongoCriteria = mergeCriteria(fields);

        // now tag on the "orQueries"
        List<NeutralQuery> orQueries = neutralQuery.getOrQueries();
        if (!orQueries.isEmpty()) {
            Criteria[] orCriteria = new Criteria[orQueries.size()];
            for (int i = 0; i < orCriteria.length; i++) {
                NeutralQuery orQuery = orQueries.get(i);
                Criteria orCriterion = convertToCriteria(entityName, orQuery, entitySchema);
                orCriteria[i] = orCriterion; 
            }
            mongoCriteria.orOperator(orCriteria);
        }

        return mongoCriteria;
    }

    /**
     * Merge criteria into one using the 'and' operator
     * @param criteriaForFields The criteria for fields
     * @return The updated mongo query
     */
    protected Criteria mergeCriteria(Map<String, List<NeutralCriteria>> criteriaForFields) {

        // Gather the criteria from the chain. 
        List<Criteria> toMerge = new ArrayList<Criteria>();
        if (criteriaForFields != null) {
            for (Map.Entry<String, List<NeutralCriteria>> e : criteriaForFields.entrySet()) {
                List<NeutralCriteria> list = e.getValue();

                if (list != null) {
                    Criteria fullCriteria = null;
                    for (NeutralCriteria criteria : list) {
                        fullCriteria = operatorImplementations.get(
                                criteria.getOperator()).generateCriteria(criteria, fullCriteria);
                    }

                    toMerge.add(fullCriteria);
                }
            }
        }

        // merge the criteria as needed.  
        if (toMerge.isEmpty()) {
            return new Criteria();
        } else if (toMerge.size() == 1) {
            return toMerge.get(0);
        } else {
            Criteria criterion = toMerge.get(0);
            Criteria[] otherCriteria = new Criteria[toMerge.size() - 1];
            for (int i = 1; i < toMerge.size(); i++) {
                otherCriteria[i - 1] = toMerge.get(i);
            }
            criterion.andOperator(otherCriteria);
            return criterion;
        }
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
                throw new RuntimeException("Unknown Schema Type: " + schema.getSchemaType());
            }
        }
    }

    private NeutralSchema getFieldSchema(NeutralSchema schema, String dottedField) {
        for (String field : dottedField.split("\\.")) {
            schema = this.getNestedSchema(schema, field);
            if (schema != null) {
                LOG.debug("nested schema type is {}", schema.getSchemaType());
            } else {
                LOG.debug("nested schema type is {}", "NULL");
            }
        }

        return schema;
    }

}
