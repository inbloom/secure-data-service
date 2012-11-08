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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.transformation.normalization.ContextTaker;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizerFlag;
import org.slc.sli.ingestion.transformation.normalization.IdResolutionException;
import org.slc.sli.ingestion.transformation.normalization.Ref;
import org.slc.sli.ingestion.transformation.normalization.RefDef;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Resolver for deterministic id resolution.
 *
 * @author jtully
 * @author vmcglaughlin
 *
 */
@Component
public class DeterministicIdResolver {

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    private DidSchemaParser didSchemaParser;

    @Autowired
    private DidEntityConfigReader didConfigReader;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private EntityConfigFactory entityConfigurations;

    @Autowired
    private ContextTaker contextTaker;

    private static final Logger LOG = LoggerFactory.getLogger(DeterministicIdResolver.class);

    public void resolveInternalIds(Entity entity, String tenantId, ErrorReport errorReport) {

        if (IdNormalizerFlag.useOldNormalization) {
            // TODO: remove IdNormalizerFlag
            return;
        }

        DidEntityConfig entityConfig = getEntityConfig(entity.getType());

        if (entityConfig == null) {
            return;
        }

        if (entityConfig.getReferenceSources() == null || entityConfig.getReferenceSources().isEmpty()) {
            LOG.warn("Entity configuration contains no references --> returning...");
            return;
        }

        String collectionName = "";
        String referenceEntityType = "";
        String sourceRefPath = "";

        for (DidRefSource didRefSource : entityConfig.getReferenceSources()) {
            try {
                referenceEntityType = didRefSource.getEntityType();
                sourceRefPath = didRefSource.getSourceRefPath();
                NeutralSchema schema = schemaRepository.getSchema(referenceEntityType);
                if (schema != null && schema.getAppInfo() != null) {
                    collectionName = schema.getAppInfo().getCollectionType();
                }

                handleDeterministicIdForReference(entity, didRefSource, collectionName, tenantId);

            } catch (IdResolutionException e) {
                handleException(sourceRefPath, entity.getType(), referenceEntityType, e, errorReport);
            }
        }
    }

    private DidEntityConfig getEntityConfig(String entityType) {
        DidEntityConfig configFromParser = didSchemaParser == null ? null : didSchemaParser.getEntityConfigs().get(entityType);
        DidEntityConfig configByHand = didConfigReader == null ? null : didConfigReader.getDidEntityConfiguration(entityType);
        DidEntityConfig retVal = new DidEntityConfig();
        if (configFromParser != null && configFromParser.getReferenceSources() != null) {
            retVal.getReferenceSources().addAll(configFromParser.getReferenceSources());
        }
        if (configByHand != null && configByHand.getReferenceSources() != null) {
            retVal.getReferenceSources().addAll(configByHand.getReferenceSources());
        }
        return retVal;
    }

    private DidRefConfig getRefConfig(String refType) {
        return didSchemaParser.getRefConfigs().get(refType);
    }

    private void handleDeterministicIdForReference(Entity entity, DidRefSource didRefSource, String collectionName,
            String tenantId) throws IdResolutionException {

        String entityType = didRefSource.getEntityType();
        String sourceRefPath = didRefSource.getSourceRefPath();

        DidRefConfig didRefConfig = getRefConfig(entityType);

        if (didRefConfig == null) {
            return;
        }

        Object referenceObject = getProperty(entity, sourceRefPath);

        if (referenceObject == null) {
            // ignore an empty reference if it is optional
            if (didRefSource.isOptional()) {
                return;
            } else {
                throw new IdResolutionException("Entity missing key", sourceRefPath, null);
            }
        }

        if (referenceObject instanceof List) {
            // handle a list of reference objects
            @SuppressWarnings("unchecked")
            List<Object> refList = (List<Object>) referenceObject;
            List<String> uuidList = new ArrayList<String>();

            for (Object reference : refList) {
                @SuppressWarnings("unchecked")
                String uuid = getId((Map<String, Object>) reference, tenantId, didRefConfig);
                if (uuid != null && !uuid.isEmpty()) {
                    uuidList.add(uuid);
                    addContext(entity, uuid, didRefConfig, collectionName);
                } else {
                    throw new IdResolutionException("Null or empty deterministic id generated", sourceRefPath, uuid);
                }
            }
            setProperty(entity, sourceRefPath, uuidList);
        } else if (referenceObject instanceof Map) {
            // handle a single reference object
            @SuppressWarnings("unchecked")
            Map<String, Object> reference = (Map<String, Object>) referenceObject;

            String uuid = getId(reference, tenantId, didRefConfig);
            if (uuid != null && !uuid.isEmpty()) {
                setProperty(entity, sourceRefPath, uuid);
                addContext(entity, uuid, didRefConfig, collectionName);
            } else {
                throw new IdResolutionException("Null or empty deterministic id generated", sourceRefPath, uuid);
            }
        } else {
            throw new IdResolutionException("Unsupported reference object type", sourceRefPath, null);
        }
    }

    private Object getProperty(Object bean, String sourceRefPath) throws IdResolutionException {
        Object referenceObject;
        try {
            referenceObject = PropertyUtils.getProperty(bean, sourceRefPath);
        } catch (IllegalArgumentException e) {
            throw new IdResolutionException("Unable to pull reference object from entity", sourceRefPath, null, e);
        } catch (IllegalAccessException e) {
            throw new IdResolutionException("Unable to pull reference object from entity", sourceRefPath, null, e);
        } catch (InvocationTargetException e) {
            throw new IdResolutionException("Unable to pull reference object from entity", sourceRefPath, null, e);
        } catch (NoSuchMethodException e) {
            throw new IdResolutionException("Unable to pull reference object from entity", sourceRefPath, null, e);
        }

        return referenceObject;
    }

    private void setProperty(Object bean, String fieldPath, Object uuid) throws IdResolutionException {
        try {
            PropertyUtils.setProperty(bean, fieldPath, uuid);
        } catch (IllegalAccessException e) {
            throw new IdResolutionException("Unable to set reference object for entity", fieldPath, uuid.toString(), e);
        } catch (InvocationTargetException e) {
            throw new IdResolutionException("Unable to set reference object for entity", fieldPath, uuid.toString(), e);
        } catch (NoSuchMethodException e) {
            throw new IdResolutionException("Unable to set reference object for entity", fieldPath, uuid.toString(), e);
        }
    }

    // This logic would ideally be performed outside of this class.
    // However, this would duplicate the setup (looping, etc) already present here.
    // This logic will be removed in the near future, so not refactoring.
    private void addContext(Entity entity, String uuid, DidRefConfig didRefConfig, String collectionName) {
        EntityConfig oldEntityConfig = entityConfigurations.getEntityConfiguration(entity.getType());
        if (oldEntityConfig != null && oldEntityConfig.getReferences() != null) {
            for (RefDef rd : oldEntityConfig.getReferences()) {
                Ref ref = rd.getRef();
                if (ref != null && ref.getTakesContext() != null
                        && ref.getEntityType().equals(didRefConfig.getEntityType())) {
                    Criteria criteria = Criteria.where("_id").is(uuid);
                    Query filter = new Query(criteria);
                    List<String> ids = new ArrayList<String>();
                    List<String> takesContext = ref.getTakesContext();

                    contextTaker.addContext(entity, takesContext, collectionName, filter, ids);
                }
            }
        }
    }

    private void handleException(String sourceRefPath, String entityType, String referenceType, Exception e,
            ErrorReport errorReport) {
        LOG.error("Error accessing indexed bean property " + sourceRefPath + " for bean " + entityType, e);
        String errorMessage = "ERROR: Failed to resolve a deterministic id" + "\n       Entity " + entityType
                + ": Reference to " + referenceType
                + " is incomplete because the following reference field is not resolved: " + sourceRefPath;

        errorReport.error(errorMessage, this);
    }

    // function which, given reference type map (source object) and refConfig, return a did
    private String getId(Map<String, Object> reference, String tenantId, DidRefConfig didRefConfig)
            throws IdResolutionException {
        if (didRefConfig.getEntityType() == null || didRefConfig.getEntityType().isEmpty()) {
            return null;
        }

        if (didRefConfig.getKeyFields() == null || didRefConfig.getKeyFields().isEmpty()) {
            return null;
        }

        Map<String, String> naturalKeys = new HashMap<String, String>();

        for (KeyFieldDef keyFieldDef : didRefConfig.getKeyFields()) {
            // populate naturalKeys
            Object value = null;
            if (keyFieldDef.getRefConfig() != null) {
                Object nestedRef = getProperty(reference, keyFieldDef.getValueSource());

                if (nestedRef == null) {
                    if (keyFieldDef.isOptional() == false) {
                        throw new IdResolutionException("No value found for required reference",
                                keyFieldDef.getValueSource(), "");
                    } else {
                        // since it's an optional field, replace it with "" in the natural key list
                        value = "";
                    }
                    // otherwise, continue to end of loop with null 'value'
                } else {
                    if (nestedRef instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> nestedRefMap = (Map<String, Object>) nestedRef;
                        value = getId(nestedRefMap, tenantId, keyFieldDef.getRefConfig());
                    } else {
                        throw new IdResolutionException("Non-map value found from entity",
                                keyFieldDef.getValueSource(), "");
                    }
                }

            } else {
            	value = getProperty(reference, keyFieldDef.getValueSource());
            }

            String fieldName = keyFieldDef.getKeyFieldName();
            // don't add null or empty keys to the naturalKeys map
            if (fieldName != null && !fieldName.isEmpty() && (value != null || keyFieldDef.isOptional())) {
                naturalKeys.put(fieldName, value == null ? "" : value.toString());
            }
        }

        // no natural keys found
        if (naturalKeys.isEmpty()) {
            return null;
        }

        // TODO: need to verify this
        String parentId = null;
        String entityType = didRefConfig.getEntityType();
        if (EmbeddedDocumentRelations.getSubDocuments().contains(entityType)) {
            String parentKey = EmbeddedDocumentRelations.getParentFieldReference(entityType);
            parentId = naturalKeys.get(parentKey);
        }

        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeys, tenantId,
                didRefConfig.getEntityType(), parentId);
        return uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
    }

    public DidSchemaParser getDidSchemaParser() {
        return didSchemaParser;
    }

    public void setDidSchemaParser(DidSchemaParser didSchemaParser) {
        this.didSchemaParser = didSchemaParser;
    }

}
