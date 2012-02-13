package org.slc.sli.ingestion.handler;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Handles the persisting of Entity objects
 *
 * @author dduran
 *         Modified by Thomas Shewchuk (PI3 US811)
 *         - 2/1/2010 Added record DB lookup and update capabilities, and support for association
 *         entities.
 *
 */
public class EntityPersistHandler extends AbstractIngestionHandler<NeutralRecordEntity, Entity> {

    // private static final Logger LOG = LoggerFactory.getLogger(EntityPersistHandler.class);

    private static final String METADATA_BLOCK = "metaData";
    private static final String REGION_ID_FIELD = "idNamespace";
    private static final String EXTERNAL_ID_FIELD = "externalId";

    // Hard-code region ID here for now, until it is set for real!
    private static final String REGION_ID = "dc=slidev,dc=net";

    private EntityRepository entityRepository;

    @Override
    Entity doHandling(NeutralRecordEntity entity, ErrorReport errorReport) {

        // Okay, so for now, we're hard-coding the region into the meta data!
        entity.setMetaDataField(REGION_ID_FIELD, REGION_ID);

        // Get the region ID.
        Map<String, String> filterFields = new HashMap<String, String>();
        String regionId = entity.getMetaData().get(REGION_ID_FIELD).toString();
        filterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, regionId);

        // Process record as association or simple.
        String collectionName = null;
        if (isAssociation(entity)) {
            // Lookup each associated entity in the data store.
            Map<String, String> associatedFilterFields = new HashMap<String, String>();
            associatedFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, regionId);
            for (Map.Entry<String, Object> associatedEntityReference : entity.getLocalParentIds().entrySet()) {
                String localParentIdName = associatedEntityReference.getKey();
                collectionName = localParentIdName.toLowerCase();
                String externalId = associatedEntityReference.getValue().toString();
                associatedFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, externalId);
                Iterable<Entity> found = entityRepository.findByPaths(collectionName, associatedFilterFields);
                if (found != null && found.iterator().hasNext()) {  // Associated entity exists in data store.
                    // Get associated entity UUID.
                    String uuid = found.iterator().next().getEntityId();

                    // Replace associated entity client id with its UUID in association entity.
                    String associatedEntityClientId = collectionName + "Id";
                    entity.setAttributeField(associatedEntityClientId, uuid);

                    // Set up search fields for next associated entity and association entity.
                    associatedFilterFields.remove(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD);
                    filterFields.put("body." + associatedEntityClientId, externalId);
                } else {  // Error! Associated entity not in data store!
                    // Generate error message and process next entity in ingestion stream.
                    errorReport.error("Error: Region " + regionId + ": " + localParentIdName + " " + externalId
                            + " does not exist in data store.", EntityPersistHandler.class);  // TODO:
                                                                                             // Externalize!
                    return null;
                }
            }

            // Lookup association entity in data store using UUIDs.
            collectionName = entity.getType();
        } else {  // Simple entity.
            // Add external ID to entity's meta data.
            entity.setMetaDataField(EXTERNAL_ID_FIELD, entity.getLocalId());

            // Lookup entity in data store using its unique id fields.
            collectionName = entity.getType();
            filterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, entity.getMetaData().get(EXTERNAL_ID_FIELD).toString());
        }

        // Update or create entity in data store.
        Iterable<Entity> found = entityRepository.findByPaths(collectionName, filterFields);
        if (found != null && found.iterator().hasNext()) {  // Entity exists in data store.
            // Update entity in data store.
            Entity foundNeutral = found.iterator().next();
            entity.setEntityId(foundNeutral.getEntityId());

            if (!entityRepository.update(collectionName, entity)) {
                // TODO: exception should be replace with some logic.
                throw new RuntimeException("Record was not updated properly.");
            }

            return entity;
        } else {  // Entity not in data store.
            // Create entity in data store.
            Entity created = entityRepository.create(entity.getType(), entity.getBody(), entity.getMetaData(),
                    collectionName);
            return created;
        }
    }

    private boolean isAssociation(NeutralRecordEntity entity) {
        return entity.isAssociation();
    }

    public void setEntityRepository(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

}
