package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;

/**
 * Internal ID resolver.
 *
 * @author okrook
 *
 */
public class IdNormalizer {
    private static final Logger LOG = LoggerFactory.getLogger(IdNormalizer.class);

    private static final String METADATA_BLOCK = "metaData";

    private Repository<Entity> entityRepository;

    public void resolveInternalIds(Entity entity, String tenantId, EntityConfig entityConfig, ErrorReport errorReport) {
        if (entityConfig.getReferences() == null) {
            return;
        }

        String resolvedReferences = "";

        try {
            for (RefDef reference : entityConfig.getReferences()) {

                resolvedReferences += "       collectionName = " + reference.getRef().getCollectionName();

                for (List<Field> fields : reference.getRef().getChoiceOfFields()) {
                    for (Field field : fields) { 
                        for (FieldValue fv : field.getValues()) {
                            if (fv.getRef() == null) {
                                Object entityValue = PropertyUtils.getProperty(entity, fv.getValueSource());
                                if (!(entityValue instanceof Collection)) {
                                    resolvedReferences += ", value = " + entityValue.toString() + "\n";
                                }
                            }
                        }
                    }
                }

                String id = resolveInternalId(entity, tenantId, reference.getRef(), errorReport, resolvedReferences);

                if (errorReport.hasErrors()) {
                    return;
                }

                PropertyUtils.setProperty(entity, reference.getFieldPath(), id);
            }
        } catch (Exception e) {
            LOG.error("Error accessing property", e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n"
                                + "       Entity   " + entity.getType() + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals ("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                              + resolvedReferences;
            }

            errorReport.error(errorMessage, this);
        }
    }

    public String resolveInternalId(Entity entity, String tenantId, Ref refConfig, 
                                    ErrorReport errorReport, String resolvedReferences) {
        List<String> ids = resolveReferenceInternalIds(entity, tenantId, refConfig, errorReport);

        if (ids.size() == 0) {

            String errorMessage = "ERROR: Failed to resolve a reference" + "\n"
                                + "       Entity   " + entity.getType() + "\n"
                                + "       Field    " + refConfig.getCollectionName() + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals ("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                              + resolvedReferences;
            }

            errorReport.error(errorMessage, this);
            return null;
        }

        return ids.get(0);
    }

    private List<String> resolveReferenceInternalIds(Entity entity, String tenantId, Ref refConfig, ErrorReport errorReport) {
        ProxyErrorReport proxyErrorReport = new ProxyErrorReport(errorReport);

        String collection = refConfig.getCollectionName();

        Query filter = new Query();

        try {
            for (List<Field> fields : refConfig.getChoiceOfFields()) {
                Query choice = new Query();

                choice.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(tenantId));

                for (Field field : fields) {
                    List<Object> filterValues = new ArrayList<Object>();

                    for (FieldValue fv : field.getValues()) {
                        if (fv.getRef() != null) {
                            filterValues.addAll(resolveReferenceInternalIds(entity, tenantId, fv.getRef(), proxyErrorReport));
                        } else {
                            Object entityValue = PropertyUtils.getProperty(entity, fv.getValueSource());
                            if (entityValue instanceof Collection) {
                                Collection<?> entityValues = (Collection<?>) entityValue;
                                filterValues.addAll(entityValues);
                            } else {
                                filterValues.add(entityValue.toString());
                            }
                        }
                    }

                    choice.addCriteria(Criteria.where(field.getPath()).in(filterValues));
                }

                filter.or(choice);
            }
        } catch (Exception e) {
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference", this);
        }

        if (proxyErrorReport.hasErrors()) {
            return null;
        }

        Iterable<Entity> foundRecords = entityRepository.findByQuery(collection, filter, 0, 0);

        List<String> ids = new ArrayList<String>();

        if (foundRecords != null && foundRecords.iterator().hasNext()) {
            for (Entity record : foundRecords) {
                ids.add(record.getEntityId());
            }
        }

        return ids;
    }

    /**
     * @return the entityRepository
     */
    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    /**
     * @param entityRepository the entityRepository to set
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

}
