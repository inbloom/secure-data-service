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

package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.cache.CacheProvider;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Internal ID resolver.
 *
 *
 *
 * @author okrook
 *
 */

@Component
public class IdNormalizer {
    private static final Logger LOG = LoggerFactory.getLogger(IdNormalizer.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String CACHE_NAMESPACE = "newId";

    @Autowired
    @Qualifier(value = "mongoEntityRepository")
    private Repository<Entity> entityRepository;

    @Autowired
    private CacheProvider cacheProvider;

    @Autowired
    private EntityConfigFactory entityConfigurations;

    @Autowired
    private SchemaRepository schemaRepository;

    public void resolveInternalIds(Entity entity, String tenantId, EntityConfig entityConfig, ErrorReport errorReport) {

        if (entityConfig == null) {
            LOG.warn("Entity configuration is null --> returning...");
            return;
        }

        if (entityConfig.getReferences() == null) {
            LOG.debug("Entity configuration contains no references --> checking for sub-entities and then returning...");
            resolveSubEntities(entity, tenantId, entityConfig, errorReport);
            return;
        }

        String resolvedReferences = "";
        String collectionName = "";

        try {
            for (RefDef reference : entityConfig.getReferences()) {

                // Don't resolve references marked as deprecated, allows the transition to
                // deterministicIdResolution
                // TODO: uncomment when deterministic id reference resolution should be activated

                if (reference.isDeprecated()) {
                    // TODO: remove IdNormalizerFlag
                    if (!IdNormalizerFlag.useOldNormalization) {
                        continue;
                    }
                    // override deprecated flag, complete processing
                }

                int numRefInstances = getNumRefInstances(entity, reference.getRef());
                NeutralSchema schema = schemaRepository.getSchema(reference.getRef().getEntityType());
                if (schema != null) {
                    AppInfo appInfo = schema.getAppInfo();
                    if (appInfo != null) {
                        collectionName = appInfo.getCollectionType();
                    }
                }

                resolvedReferences += "       collectionName = " + collectionName;

                for (List<Field> fields : reference.getRef().getChoiceOfFields()) {
                    for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                        for (Field field : fields) {
                            for (FieldValue fv : field.getValues()) {
                                if (fv.getRef() == null) {
                                    String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(),
                                            reference.getRef(), refIndex, refIndex, fv.getRef());
                                    try {
                                        Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                                        if (entityValue != null) {
                                            if (!(entityValue instanceof Collection)) {
                                                resolvedReferences += ", value = " + entityValue.toString() + "\n";
                                            }
                                        }
                                    } catch (Exception e) {
                                        if (!reference.getRef().isOptional()) {
                                            LOG.error("Error accessing indexed bean property " + valueSourcePath
                                                    + " for bean " + entity.getType());
                                            String errorMessage = "ERROR: Failed to resolve a reference"
                                                    + "\n"
                                                    + "       Entity "
                                                    + entity.getType()
                                                    + ": Reference to "
                                                    + collectionName
                                                    + " is incomplete because the following reference field is not resolved: "
                                                    + valueSourcePath.substring(valueSourcePath.lastIndexOf('.') + 1);

                                            errorReport.error(errorMessage, this);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                String fieldPath = reference.getFieldPath();

                List<String> ids = resolveReferenceInternalIds(entity, tenantId, reference.getRef(), fieldPath,
                        errorReport);

                if (ids == null || ids.size() == 0) {
                    if (!reference.getRef().isOptional() && (numRefInstances > 0)) {
                        LOG.error("Error with entity " + entity.getType() + " missing required reference "
                                + collectionName);
                        String errorMessage = "ERROR: Missing required reference" + "\n" + "       Entity "
                                + entity.getType() + ": Missing reference to " + collectionName;

                        errorReport.error(errorMessage, this);
                    }
                    continue;
                }

                if (ids.size() != numRefInstances) {
                    LOG.error("Error in number of resolved internal ids for entity " + entity.getType() + ": Expected "
                            + numRefInstances + ", got " + ids.size() + " references to " + collectionName);
                    String errorMessage = "ERROR: Failed to resolve expected number of references" + "\n"
                            + "       Entity " + entity.getType() + ": Expected " + numRefInstances + ", got "
                            + ids.size() + " references to " + collectionName;

                    errorReport.error(errorMessage, this);
                }

                if (errorReport.hasErrors()) {
                    continue;
                }

                if (reference.getRef().isRefList()) {
                    // for lists of references set the properties on each element of the
                    // resolved ID list
                    for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                        String indexedFieldPath = fieldPath + ".[" + Integer.toString(refIndex) + "]";
                        PropertyUtils.setProperty(entity, indexedFieldPath, ids.get(refIndex));
                    }
                } else {
                    PropertyUtils.setProperty(entity, reference.getFieldPath(), ids.get(0));
                }

            }
        } catch (Exception e) {
            LogUtil.error(LOG, "Error resolving reference to " + collectionName + " in " + entity.getType(), e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity " + entity.getType()
                    + ": Reference to " + collectionName + " cannot be resolved" + "\n";
            if (resolvedReferences != null && !resolvedReferences.equals("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                        + resolvedReferences;
            }

            errorReport.error(errorMessage, this);
        }
        resolveSubEntities(entity, tenantId, entityConfig, errorReport);
    }

    private void resolveSubEntities(Entity entity, String tenantId, EntityConfig entityConfig, ErrorReport errorReport) {
        Map<String, String> subEntityConfigs = entityConfig.getSubEntities();
        if (subEntityConfigs != null) {
            LOG.debug("Checking entity: {} for sub-entities: {}", entity.getType(), subEntityConfigs);
            for (Map.Entry<String, String> entry : subEntityConfigs.entrySet()) {
                String pathString = entry.getKey();
                boolean optional = pathString.endsWith("?");
                String path = optional ? pathString.substring(0, pathString.length() - 1) : pathString;
                EntityConfig subEntityConfig = entityConfigurations.getEntityConfiguration(entry.getValue());
                LOG.debug("Checking sub-entity: {} [optional: {}]", pathString, optional);
                try {
                    Object subEntityObject = PropertyUtils.getProperty(entity, path);
                    if (subEntityObject == null) {
                        if (optional) {
                            continue;
                        } else {
                            errorReport.error("Unable to find property " + path, this);
                        }
                    }
                    if (subEntityObject instanceof List) {
                        LOG.debug("Resolving list of sub-entities.");
                        for (Object subEntityInstance : (List<?>) subEntityObject) {
                            resolveSubEntity(tenantId, errorReport, subEntityConfig, subEntityInstance);
                        }
                    } else {
                        LOG.debug("Resolving single sub-entity.");
                        resolveSubEntity(tenantId, errorReport, subEntityConfig, subEntityObject);
                    }
                } catch (Exception e) {
                    LogUtil.error(LOG, "Error parsing " + entity, e);
                }
            }
        } else {
            LOG.debug("Entity: {} does not have any sub-entities.", entity.getType());
        }
    }

    @SuppressWarnings("unchecked")
    protected void resolveSubEntity(String tenantId, ErrorReport errorReport, EntityConfig subEntityConfig,
            Object subEntityInstance) {
        try {
            NeutralRecord nr = new NeutralRecord();
            nr.setAttributes((Map<String, Object>) subEntityInstance);
            Entity subEntity = new NeutralRecordEntity(nr);
            resolveInternalIds(subEntity, tenantId, subEntityConfig, errorReport);
        } catch (ClassCastException e) {
            LogUtil.error(LOG, "error resolving " + subEntityInstance, e);
        }
    }

    protected String resolveInternalId(Entity entity, String tenantId, Ref refConfig, String fieldPath,
            ErrorReport errorReport, String resolvedReferences) {
        LOG.debug("resolving id for {}", entity.getType());
        List<String> ids = resolveReferenceInternalIds(entity, tenantId, refConfig, fieldPath, errorReport);

        if (ids.size() == 0) {

            String collectionName = "";
            NeutralSchema schema = schemaRepository.getSchema(refConfig.getEntityType());
            if (schema != null) {
                AppInfo appInfo = schema.getAppInfo();
                if (appInfo != null) {
                    collectionName = appInfo.getCollectionType();
                }
            }

            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity " + entity.getType()
                    + ": Reference to " + collectionName + " unresolved" + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                        + resolvedReferences;
            }

            errorReport.error(errorMessage, this);

            return null;
        }

        return ids.get(0);
    }

    /**
     * Recursively resolves SLI internal id's.
     *
     * @param entity
     *            entity to have id's embedded on.
     * @param tenantId
     *            tenant of the entity.
     * @param refConfig
     *            reference configuration (json).
     * @param fieldPath
     *            field to be resolved.
     * @param errorReport
     *            error reporting.
     * @return list of strings representing resolved id's.
     */
    protected List<String> resolveReferenceInternalIds(Entity entity, String tenantId, Ref refConfig, String fieldPath,
            ErrorReport errorReport) {
        int numRefInstances = 1;
        try {
            numRefInstances = getNumRefInstances(entity, refConfig);
        } catch (Exception e) {
            errorReport.error("Failed to get number of reference instances", this);
        }
        return resolveReferenceInternalIds(entity, tenantId, numRefInstances, refConfig, fieldPath, errorReport, 0,
                null);
    }

    protected List<String> resolveReferenceInternalIds(Entity entity, String tenantId, int numRefInstances,
            Ref refConfig, String fieldPath, ErrorReport errorReport, int parentIndex, Ref parentRefConfig) {

        ProxyErrorReport proxyErrorReport = new ProxyErrorReport(errorReport);

        ArrayList<Criteria> queryOrList = new ArrayList<Criteria>();
        String collection = "";
        NeutralSchema schema = schemaRepository.getSchema(refConfig.getEntityType());
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                collection = appInfo.getCollectionType();
            }
        }

        try {
            // if the reference is a list of references loop over all elements adding an 'or' query
            // statement for each
            for (List<Field> fields : refConfig.getChoiceOfFields()) {

                for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                    Criteria choice = new Criteria();
                    List<Criteria> andList = new ArrayList<Criteria>();
                    for (Field field : fields) {
                        List<Object> filterValues = new ArrayList<Object>();

                        for (FieldValue fv : field.getValues()) {
                            if (fv.getRef() != null) {
                                boolean isEmptyRef = isEmptyRef(entity, fv.getRef(), refIndex, refConfig);
                                List<String> resolvedIds = null;
                                if (!isEmptyRef) {
                                    resolvedIds = resolveReferenceInternalIds(entity, tenantId, numRefInstances,
                                            fv.getRef(), fieldPath, proxyErrorReport, refIndex, refConfig);
                                }

                                // it is acceptable for a child reference to not be resolved iff it
                                // is
                                // an optional reference and the source is empty
                                // otherwise fail the parent reference by returning an empty list
                                if (resolvedIds != null && resolvedIds.size() > 0) {
                                    filterValues.addAll(resolvedIds);
                                } else if (!fv.getRef().isOptional() || !isEmptyRef) {
                                    return new ArrayList<String>();
                                }
                            } else {
                                String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), refConfig,
                                        refIndex, parentIndex, parentRefConfig);
                                try {
                                    Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);

                                    if (entityValue != null) {
                                        if (field.getIsList()) {
                                            BasicDBList entitySourceValueDBList = (BasicDBList) entityValue;
                                            if (entitySourceValueDBList != null) {

                                                for (Object object : entitySourceValueDBList) {
                                                    BasicDBObject dbObject = (BasicDBObject) object;
                                                    if (dbObject == null) {
                                                        continue;
                                                    }

                                                    Object keyObject = dbObject.get(field.getEntityKey());
                                                    if (keyObject == null) {
                                                        continue;
                                                    }
                                                    BasicDBList keyList = (BasicDBList) keyObject;
                                                    for (Object queryObject : keyList) {
                                                        BasicDBObject queryDbObject = (BasicDBObject) queryObject;
                                                        if (queryDbObject == null) {
                                                            continue;
                                                        }

                                                        for (Object keyObj : queryDbObject.toMap().keySet()) {
                                                            if (keyObj == null) {
                                                                continue;
                                                            }
                                                            LOG.debug(keyObj.toString());
                                                            if (field.getQueryList().containsKey(keyObj.toString())) {
                                                                andList.add(Criteria.where(
                                                                        field.getQueryList().get(keyObj.toString()))
                                                                        .is(queryDbObject.toMap().get(keyObj)));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            if (entityValue instanceof Collection) {
                                                Collection<?> entityValues = (Collection<?>) entityValue;
                                                filterValues.addAll(entityValues);
                                            } else {
                                                filterValues.add(entityValue);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    if (!refConfig.isOptional()) {
                                        LogUtil.error(LOG, "Error accessing indexed bean property " + valueSourcePath
                                                + " for bean " + entity.getType(), e);
                                        String errorMessage = "ERROR: Failed to resolve a reference"
                                                + "\n"
                                                + "       Entity "
                                                + entity.getType()
                                                + ": Reference to "
                                                + collection
                                                + " is incomplete because the following reference field is not resolved: "
                                                + valueSourcePath.substring(valueSourcePath.lastIndexOf('.') + 1);

                                        errorReport.error(errorMessage, this);
                                    }
                                }
                            }
                        }
                        if (filterValues.size() > 0) {
                            LOG.debug("adding criteria for {}", field.getPath());
                            andList.add(Criteria.where(field.getPath()).in(filterValues));
                        }
                    }
                    if (andList.size() > 0) {
                        for (Criteria criteria : andList) {
                            choice.and(criteria.getKey()).is(criteria.getCriteriaObject().get(criteria.getKey()));
                        }
                        queryOrList.add(choice);
                    }
                }
            }
        } catch (Exception e) {
            if (refConfig.isOptional()) {
                return new ArrayList<String>();
            }
            LogUtil.error(LOG, "Error resolving reference to " + fieldPath + " in " + entity.getType(), e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity " + entity.getType()
                    + ": Reference to " + collection + " unresolved";

            proxyErrorReport.error(errorMessage, this);
        }

        if (proxyErrorReport.hasErrors() || queryOrList.size() == 0) {
            return null;
        }

        // combine the queries with or
        Query filter = Query.query(new Criteria().orOperator(queryOrList.toArray(new Criteria[queryOrList.size()])));
        List<String> ids = new ArrayList<String>();
        ids.addAll(checkInCache(collection, tenantId, filter));

        if (CollectionUtils.isEmpty(ids)) {
            @SuppressWarnings("deprecation")
            Iterable<Entity> foundRecords = entityRepository.findByQuery(collection, filter, 0, 0);

            if (foundRecords != null && foundRecords.iterator().hasNext()) {
                for (Entity record : foundRecords) {
                    ids.add(record.getEntityId());
                }
            }
            cache(ids, collection, tenantId, filter);
        }

        // sort because the $or query can produce different results every time
        Collections.sort(ids);
        return ids;
    }

    /**
     * Resolves a reference represented by an array of complex objects, which
     *
     * @param entity
     *            - the referer entity
     * @param tenantId
     *            - tenant's id
     * @param valueSource
     *            - xpath to the complex object array in the referer entity
     * @param fieldPath
     *            - xpath to the field in the referer entity where the resolved id will be written
     *            into
     * @param targetCollection
     *            - referenced entity
     * @param path
     *            - xpath to the complex object array in the referenced entity
     * @param complexFieldNames
     *            - names of fields in the complex object
     * @param errorReport
     *            - error reporter
     */
    public void resolveReferenceWithComplexArray(Entity entity, String tenantId, String valueSource, String fieldPath,
            String collectionName, String path, List<String> complexFieldNames, ErrorReport errorReport) {

        try {
            List<?> refValues = (List<?>) PropertyUtils.getProperty(entity, valueSource);

            // Overall query
            Query query = new Query();
            ArrayList<Criteria> queryOrList = new ArrayList<Criteria>();

            // For each element in the referer's array, create a subQuery
            // Then OR them together to make a single mongo query
            for (int refIndex = 0; refIndex < refValues.size(); refIndex++) {
                String valueSourcePath = valueSource + ".[" + Integer.toString(refIndex) + "]";

                // Create the fieldValueCriteria for matching this complex object
                Criteria fieldValueCriteria = null;
                for (String fieldName : complexFieldNames) {
                    Object fieldValue = PropertyUtils.getProperty(entity, valueSourcePath + "." + fieldName);
                    if (fieldValue == null) {
                        continue;
                    }
                    if (fieldValueCriteria == null) {
                        fieldValueCriteria = Criteria.where(fieldName).is(fieldValue);
                    } else {
                        fieldValueCriteria = fieldValueCriteria.and(fieldName).is(fieldValue);
                    }
                }
                if (fieldValueCriteria == null) {
                    continue;
                }
                Criteria criteria = new Criteria();
                criteria = criteria.and(path).elemMatch(fieldValueCriteria);
                // add the subquery to overall query
                queryOrList.add(criteria);
            }

            // combine the queries with or
            query.addCriteria((new Criteria()).orOperator(queryOrList.toArray(new Criteria[queryOrList.size()])));

            // execute query and record results
            Set<String> foundIds = new LinkedHashSet<String>();
            @SuppressWarnings("deprecation")
            Iterable<Entity> foundRecords = entityRepository.findByQuery(collectionName, query, 0, 0);

            for (Entity record : foundRecords) {
                foundIds.add(record.getEntityId());
            }

            // resolution fails if not exactly one resolved object is found.
            if (foundIds.size() != 1) {
                throw new RuntimeException("Number of resolved ids in resolve complex reference is not 1, but is "
                        + foundIds.size());
            } else {
                PropertyUtils.setProperty(entity, fieldPath, foundIds.iterator().next());
            }

        } catch (Exception e) {
            LogUtil.error(LOG, "Error resolving reference to " + collectionName + " in " + entity.getType(), e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity " + entity.getType()
                    + ": Reference to " + collectionName + " cannot be resolved" + "\n";
            errorReport.error(errorMessage, this);
        }

    }

    private void cache(List<String> ids, String collection, String tenantId, Query filter) {
        String key = composeKey(collection, tenantId, filter);

        cacheProvider.add(key, ids);

    }

    /**
     * Check for this in the cache
     *
     * @param collection
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> checkInCache(String collection, String tenantId, Query filter) {
        List<String> ids;
        String key = composeKey(collection, tenantId, filter);
        Object val = cacheProvider.get(key);

        if (val == null) {
            ids = new ArrayList<String>();
        } else {
            ids = (List<String>) val;
        }

        return ids;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String composeKey(String collection, String tenantId, Query filter) {

        Map map = filter.getQueryObject().toMap();

        SortedMap<?, ?> sortedMap = new TreeMap();
        sortedMap.putAll(map);

        String hash = DigestUtils.sha256Hex(sortedMap.toString());
        return String.format("%s_%s_%s_%s", CACHE_NAMESPACE, collection, tenantId, hash);

    }

    /**
     * @return the entityRepository
     */
    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    /**
     * @param entityRepository
     *            the entityRepository to set
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * Returns the number of reference instances of a Ref object in a given entity
     */
    private int getNumRefInstances(Entity entity, Ref refConfig) throws Exception {

        int numRefInstances = 1;
        if (refConfig.isRefList()) {
            numRefInstances = 0;
            List<?> refValues = (List<?>) PropertyUtils.getProperty(entity, refConfig.getRefObjectPath());
            if (refValues != null) {
                Set<String> valueSet = new LinkedHashSet<String>();
                for (Object entry : refValues) {
                    valueSet.add(entry.toString());
                }
                numRefInstances = valueSet.size();
            }
        }
        return numRefInstances;
    }

    /**
     * Constructs the property name used by PropertyUtils.getProperty for indexed references
     */
    private String constructIndexedPropertyName(String valueSource, Ref refConfig, int refIndex, int parentIndex,
            Ref parentRefConfig) {
        String result = valueSource;

        if (refConfig.isRefList()) {
            result = "";
            String refObjectPath = refConfig.getRefObjectPath();
            // split the valueSource by .
            StringTokenizer tokenizer = new StringTokenizer(valueSource, ".");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                result += token;
                if (result.equals(refObjectPath)) {
                    result += ".[" + Integer.toString(refIndex) + "]";
                }
                if (tokenizer.hasMoreTokens()) {
                    result += ".";
                }
            }
        }

        // UN: Parent is also a refList, add current Parent Index to the Parent RefObject Path
        if (parentRefConfig != null && parentRefConfig.isRefList()) {
            String parentRefObjectPath = parentRefConfig.getRefObjectPath();
            result = result.replaceFirst(parentRefObjectPath,
                    parentRefObjectPath + ".[" + Integer.toString(parentIndex) + "]");
        }

        return result;
    }

    public void setCacheProvider(CacheProvider c) {
        this.cacheProvider = c;
    }

    private boolean isEmptyRef(Entity entity, Ref refConfig, int parentIndex, Ref parentRef) {
        for (List<Field> fields : refConfig.getChoiceOfFields()) {
            for (Field field : fields) {
                for (FieldValue fv : field.getValues()) {
                    if (fv.getRef() != null) {
                        if (!isEmptyRef(entity, fv.getRef(), parentIndex, parentRef)) {
                            return false;
                        }
                    } else {
                        String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), refConfig, 0,
                                parentIndex, parentRef);
                        Object entityValue = null;
                        try {
                            entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                        } catch (Exception e) {
                            // exceptions here indicate that the something in valueSourcePath does
                            // not exist
                            continue;
                        }
                        if (entityValue != null) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
