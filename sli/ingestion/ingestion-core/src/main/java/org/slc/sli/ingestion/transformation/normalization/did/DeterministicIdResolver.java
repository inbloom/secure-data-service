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

import org.slc.sli.common.domain.EmbedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.transformation.normalization.ContextTaker;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdResolutionException;
import org.slc.sli.ingestion.transformation.normalization.Ref;
import org.slc.sli.ingestion.transformation.normalization.RefDef;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
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

    @Autowired
    private DidSchemaParser didSchemaParser;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private EntityConfigFactory entityConfigurations;

    @Autowired
    private ContextTaker contextTaker;

    private static final Logger LOG = LoggerFactory.getLogger(DeterministicIdResolver.class);

    public void resolveInternalIds(Entity entity, String tenantId, ErrorReport errorReport) {

        DidEntityConfig entityConfig = getEntityConfig(entity.getType());

        if (entityConfig == null) {
            LOG.warn("Entity configuration is null --> returning...");
            return;
        }

        if (entityConfig.getReferenceSources() == null || entityConfig.getReferenceSources().isEmpty()) {
            LOG.debug("Entity configuration contains no references --> returning...");
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
                if (schema != null) {
                    AppInfo appInfo = schema.getAppInfo();
                    if (appInfo != null) {
                        collectionName = appInfo.getCollectionType();
                    }
                }

                handleDeterministicIdForReference(entity, didRefSource, collectionName, tenantId);

            } catch (IllegalAccessException e) {
                handleException(sourceRefPath, referenceEntityType, collectionName, e, errorReport);
            } catch (InvocationTargetException e) {
                handleException(sourceRefPath, referenceEntityType, collectionName, e, errorReport);
            } catch (NoSuchMethodException e) {
                handleException(sourceRefPath, referenceEntityType, collectionName, e, errorReport);
            } catch (IllegalArgumentException e) {
                handleException(sourceRefPath, referenceEntityType, collectionName, e, errorReport);
            } catch (IdResolutionException e) {
                handleException(sourceRefPath, referenceEntityType, collectionName, e, errorReport);
            }
        }
    }

    private DidEntityConfig getEntityConfig(String entityType) {
        return didSchemaParser.getEntityConfigs().get(entityType);
    }

    private DidRefConfig getRefConfig(String refType) {
        return didSchemaParser.getRefConfigs().get(refType);
    }

    private void handleDeterministicIdForReference(Entity entity, DidRefSource didRefSource, String collectionName, String tenantId)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IdResolutionException {
        String entityType = didRefSource.getEntityType();
        String didFieldPath = didRefSource.getDidFieldPath();
        String sourceRefPath = didRefSource.getSourceRefPath();

        DidRefConfig didRefConfig = getRefConfig(entityType);

        if (didRefConfig == null) {
             return;
        }

        Object referenceObject = PropertyUtils.getProperty(entity, sourceRefPath);
        if (referenceObject == null) {
            //ignore an empty reference if it is optional
            if (didRefSource.isOptional()) {
                return;
            } else {
                throw new IdResolutionException("Entity missing key", sourceRefPath, null);
            }
        }

        if (referenceObject instanceof List) {
            //handle a list of reference objects
            @SuppressWarnings("unchecked")
            List<Object> refList = (List<Object>) referenceObject;
            List<String> uuidList = new ArrayList<String>();

            for (Object reference :  refList) {
                @SuppressWarnings("unchecked")
                String uuid = getId((Map<String, Object>) reference, tenantId, didRefConfig);
                if (uuid != null && !uuid.isEmpty()) {
                    uuidList.add(uuid);
                    addContext(entity, uuid, didRefConfig, collectionName);
                } else {
                    // TODO key and value below aren't what we want
                    throw new IdResolutionException("Null or empty deterministic id generated", didFieldPath, uuid);
                }
            }
            PropertyUtils.setProperty(entity, didFieldPath, uuidList);
        } else {
            //handle a single reference object
            @SuppressWarnings("unchecked")
            Map<String, Object> reference = (Map<String, Object>) referenceObject;

            String uuid = getId(reference, tenantId, didRefConfig);
            if (uuid != null && !uuid.isEmpty()) {
                PropertyUtils.setProperty(entity, didFieldPath, uuid);
                addContext(entity, uuid, didRefConfig, collectionName);
            } else {
                // TODO key and value below aren't what we want
                throw new IdResolutionException("Null or empty deterministic id generated", didFieldPath, uuid);
            }
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

    private void handleException(String sourceRefPath, String entityType, String collectionName, Exception e, ErrorReport errorReport) {
        LOG.error("Error accessing indexed bean property " + sourceRefPath
                + " for bean " + entityType, e);
        String errorMessage = "ERROR: Failed to resolve a reference"
                + "\n       Entity " + entityType + ": Reference to " + collectionName
                + " is incomplete because the following reference field is not resolved: "
                + sourceRefPath.substring(sourceRefPath.lastIndexOf('.') + 1);

        errorReport.error(errorMessage, this);
    }

    // function which, given reference type map (source object) and refConfig, return a did
    private String getId(Map<String, Object> reference, String tenantId, DidRefConfig didRefConfig)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IllegalArgumentException {

        if (didRefConfig.getEntityType() == null || didRefConfig.getEntityType().isEmpty()) {
            return null;
        }

        if (didRefConfig.getKeyFields() == null || didRefConfig.getKeyFields().isEmpty()) {
            return null;
        }

        Map<String, String> naturalKeys = new HashMap<String, String>();

        for (KeyFieldDef keyFieldDef : didRefConfig.getKeyFields()) {
            // populate naturalKeys
            String value = null;
            if (keyFieldDef.getRefConfig() != null) {
                value = getId(reference, tenantId, keyFieldDef.getRefConfig());
            } else {
                value = (String) PropertyUtils.getProperty(reference, keyFieldDef.getValueSource());
            }

            String fieldName = keyFieldDef.getKeyFieldName();
            // don't add null or empty keys or values to the naturalKeys map
            if (fieldName == null || fieldName.isEmpty() || value == null || value.isEmpty()) {
                continue;
            }
            naturalKeys.put(fieldName, value);
        }

        // no natural keys found
        if (naturalKeys.isEmpty()) {
            return null;
        }

        //TODO: need to verify this
        String parentId = null;
        String entityType = didRefConfig.getEntityType();
        if (EmbedDocumentRelations.getSubDocuments().contains(entityType)) {
            String parentKey = EmbedDocumentRelations.getParentFieldReference(entityType);
            parentId = naturalKeys.get(parentKey);
        }

        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeys, tenantId, didRefConfig.getEntityType(), parentId);
        return uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
    }
}
