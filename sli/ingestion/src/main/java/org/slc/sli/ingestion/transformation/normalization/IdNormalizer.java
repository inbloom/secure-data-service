package org.slc.sli.ingestion.transformation.normalization;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.EntityRepository;
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
    private static final String REGION_ID = "https://devapp1.slidev.org:443/sp";

    private EntityRepository entityRepository;

    public String resolveInternalId(Entity entity, Ref myCollectionId, ErrorReport errorReport) {
        List<String> ids = resolveInternalIds(entity, myCollectionId, errorReport);

        if (ids.size() == 0) {
            errorReport.error("Failed to resolve a reference", this);
            return null;
        }

        return ids.get(0);
    }

    public List<String> resolveInternalIds(Entity entity, Ref myCollectionId, ErrorReport errorReport) {
        ProxyErrorReport proxyErrorReport = new ProxyErrorReport(errorReport);

        String collection = myCollectionId.getCollectionName();

        Query filter = new Query();

        try {
            for (List<Field> fields : myCollectionId.getChoiceOfFields()) {
                Query choice = new Query();

                choice.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey()))
                    .equals(REGION_ID);

                for (Field field: fields) {
                    List<String> filterValues = new ArrayList<String>();

                    for (FieldValue fv : field.getValues()) {
                        if (fv.getRef() != null) {
                            filterValues.addAll(resolveInternalIds(entity, fv.getRef(), proxyErrorReport));
                        } else {
                            filterValues.add(String.valueOf(PropertyUtils.getProperty(entity, fv.getValueSource())));
                        }
                    }

                    choice.addCriteria(Criteria.where(field.getPath()).in(filterValues));
                }

                filter.or(choice);
            }
        } catch (IllegalAccessException e) {
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference", this);
        } catch (InvocationTargetException e) {
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference", this);
        } catch (NoSuchMethodException e) {
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference", this);
        }

        if (proxyErrorReport.hasErrors()) {
            return null;
        }

        Iterable<Entity> foundRecords = entityRepository.findByQuery(collection, filter, 0, 0);

        List<String> ids = new ArrayList<String>();

        if (foundRecords != null) {
            for (Entity record : foundRecords) {
                ids.add(record.getEntityId());
            }
        }

        return ids;
    }

    /**
     * @return the entityRepository
     */
    public EntityRepository getEntityRepository() {
        return entityRepository;
    }

    /**
     * @param entityRepository the entityRepository to set
     */
    public void setEntityRepository(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

}

