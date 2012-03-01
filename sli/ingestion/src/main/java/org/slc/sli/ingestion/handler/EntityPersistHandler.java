package org.slc.sli.ingestion.handler;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.context.MessageSource;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

    // Hard-code region ID here for now, until it is set for real!
    private static final String REGION_ID = "dc=slidev,dc=net";

    private EntityRepository entityRepository;

    private MessageSource messageSource;

    @Override
    Entity doHandling(NeutralRecordEntity entity, ErrorReport errorReport) {

        // Okay, so for now, we're hard-coding the region into the meta data!
        entity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);

        matchEntity(entity, errorReport);

        if (errorReport.hasErrors()) {
            return null;
        }

        try {
            return persist(entity);
        } catch (EntityValidationException ex) {
            reportErrors(ex.getValidationErrors(), entity, errorReport);
        }

        return null;
    }

    /**
     * Persist entity in the data store.
     *
     * @param entity
     *            Entity to be persisted
     * @return Persisted entity
     * @throws EntityValidationException
     *             Validation Exception
     */
    private Entity persist(Entity entity) throws EntityValidationException {
        if (entity.getEntityId() != null) {

            if (!entityRepository.update(entity.getType(), entity)) {
                // TODO: exception should be replace with some logic.
                throw new RuntimeException("Record was not updated properly.");
            }

            return entity;
        } else {
            return entityRepository.create(entity.getType(), entity.getBody(), entity.getMetaData(), entity.getType());
        }
    }

    private void reportErrors(List<ValidationError> errors, NeutralRecordEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {
            String message = getFailureMessage("DAL_" + err.getType().name(), entity.getType(), entity.getRecordNumberInFile(),
                    err.getFieldName(), err.getFieldValue(), Arrays.toString(err.getExpectedTypes()));
            errorReport.error(message, this);
        }
    }

    /**
     * Resolve references defined by external IDs.
     *
     * @param entity
     *            Entity which has references that need to be resolved
     * @param errorReport
     *            Error reporting
     */
    public void resolveInternalIds(NeutralRecordEntity entity, ErrorReport errorReport) {
        for (Map.Entry<String, Object> externalIdEntry : entity.getLocalParentIds().entrySet()) {
            String collection = externalIdEntry.getKey().toLowerCase();
            String idNamespace = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();

            String internalId = "";

            if (Map.class.isInstance(externalIdEntry.getValue())) {

                Map<?, ?> externalSearchCriteria = (Map<?, ?>) externalIdEntry.getValue();
                internalId = resolveInternalId(collection, idNamespace, externalSearchCriteria, errorReport);

            } else {

                String externalId = externalIdEntry.getValue().toString();
                internalId = resolveInternalId(collection, idNamespace, externalId, errorReport);
            }

            if (errorReport.hasErrors()) {
                // Stop processing.
                return;
            }

            entity.setAttributeField(collection + "Id", internalId);
        }
    }

    private String resolveInternalId(String collection, String idNamespace, Map<?, ?> externalSearchCriteria, ErrorReport errorReport) {
        Map<String, String> filterFields = new HashMap<String, String>();
        filterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), idNamespace);

        Query query = new Query();
        resolveSearchCriteria(collection, filterFields, externalSearchCriteria);
        addSearchPathsToQuery(query, filterFields);
        resolveJoinCriteria(query, externalSearchCriteria, errorReport);

        Iterable<Entity> found = entityRepository.findByQuery(collection, query, 0, 1);

        if (found == null || !found.iterator().hasNext()) {
            errorReport.error(
                    "Cannot find Using Map [" + collection + "] record using the folowing filter: " + filterFields.toString() + " " + query.getQueryObject().toString(),
                    this);

            return null;
        }

        Entity entity = found.iterator().next();

        return entity.getEntityId();
    }

    private void resolveJoinCriteria(Query query,  Map<?, ?> externalSearchCriteria, ErrorReport errorReport) {
        ArrayList<Entity> newFound = new ArrayList<Entity>();


    		for (Map.Entry<?, ?> searchCriteriaEntry : externalSearchCriteria.entrySet()) {

	    		if (Map.class.isInstance(searchCriteriaEntry.getValue())) {


	    		 StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
	       	 	 String pathCollection = tokenizer.nextToken().toLowerCase();

	       	 	 String referencePath = tokenizer.nextToken();

	       	 	 Map<String, String> tempFilter = new HashMap<String, String>();
	       	 	 tempFilter.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
	       	     resolveSearchCriteria(pathCollection, tempFilter, (Map<?, ?>) searchCriteriaEntry.getValue());

	             Iterable<Entity> referenceFound = entityRepository.findByPaths(pathCollection, tempFilter);

	             if (referenceFound == null || !referenceFound.iterator().hasNext()) {
	                 errorReport.error(
	                         "Cannot find[" + pathCollection + "] record using the folowing filter: " + tempFilter.toString(),
	                         this);
	             }

	             Map<String, String> orFilter = new HashMap<String, String>();

	             for(Entity found : referenceFound){
	            	 orFilter.put(referencePath, found.getEntityId());

	             }

	             addOrToQuery(query, orFilter);

	    		}
    		}
	}

	private void resolveSearchCriteria(String collection, Map<String, String> filterFields, Map<?, ?> externalSearchCriteria) {
         for (Map.Entry<?, ?> searchCriteriaEntry : externalSearchCriteria.entrySet()) {

        	 StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
    		 String pathCollection = tokenizer.nextToken().toLowerCase();
    		 String path = tokenizer.nextToken();

    		 if(pathCollection.equals(collection)){
    			 resolveSearchCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());
	    	 }
        }
    }

	private void resolveSearchCriteria(Map<String, String> filterFields, String key, Object value) {
		if (String.class.isInstance(value)){

			StringTokenizer tokenizer = new StringTokenizer(key, "#");
	   		 String pathCollection = tokenizer.nextToken();
	   		 String newPath = tokenizer.nextToken();
			filterFields.put(newPath, value.toString());

		} else if (Map.class.isInstance(value)){

			for (Map.Entry<?, ?> searchCriteriaEntry : ((Map<?, ?>) value).entrySet()) {

	    		 resolveSearchCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());
		    }

		} else if (List.class.isInstance(value)){

			for (Object object : (List) value){

				resolveSearchCriteria(filterFields, key, object);
		    }

		}

	}

	/**
     * Resolve references defined by external IDs (from clients) with internal IDs from SLI data
     * store.
     *
     * @param collection
     *            Referenced collection
     * @param idNamespace
     *            ID namespace that uniquely identifies external ID
     * @param externalId
     *            External ID to be resolved
     * @param errorReport
     *            Error reporting
     * @return Resolved internal ID
     */
    public String resolveInternalId(String collection, String idNamespace, String externalId, ErrorReport errorReport) {
        Map<String, String> filterFields = new HashMap<String, String>();

        filterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), idNamespace);
        filterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), externalId);

        Iterable<Entity> found = entityRepository.findByPaths(collection, filterFields);
        if (found == null || !found.iterator().hasNext()) {
            errorReport.error(
                    "Cannot find [" + collection + "] record using the folowing filter: " + filterFields.toString(),
                    this);

            return null;
        }

        return found.iterator().next().getEntityId();
    }

    /**
     * Find a matched entity in the data store. If match is found the EntityID gets updated with the
     * ID from the data store.
     *
     * @param entity
     *            Entity to match
     * @param errorReport
     *            Error reporting
     */
    public void matchEntity(NeutralRecordEntity entity, ErrorReport errorReport) {
        resolveInternalIds(entity, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Map<String, String> matchFilter = createEntityLookupFilter(entity, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Iterable<Entity> match = entityRepository.findByPaths(entity.getType(), matchFilter);
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
        }
    }

    /**
     * Create look-up filter to find a matched entity in the data store.
     *
     * @param entity
     *            Entity to match in the data store
     * @param errorReport
     *            Error Reporting
     * @return Look-up filter
     */
    public Map<String, String> createEntityLookupFilter(NeutralRecordEntity entity, ErrorReport errorReport) {
        String regionId = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();

        Map<String, String> filter = new HashMap<String, String>();
        filter.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), regionId);

        if (entity.isAssociation()) {
            // Lookup each associated entity in the data store.
            for (Map.Entry<String, Object> externalReference : entity.getLocalParentIds().entrySet()) {
                String referencedCollection = externalReference.getKey().toLowerCase();
                String referencedId = referencedCollection + "Id";

                filter.put("body." + referencedId, entity.getBody().get(referencedId).toString());
            }
        } else {
            entity.setMetaDataField(EntityMetadataKey.EXTERNAL_ID.getKey(), entity.getLocalId());

            filter.put(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), entity.getLocalId().toString());
        }

        return filter;
    }

    protected String getFailureMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }

    public void setEntityRepository(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Query addSearchPathsToQuery(Query query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }

        return query;
    }

	private void addOrToQuery(Query query, Map<String, String> orFilter) {
        List<Query> queries = new ArrayList<Query>();
		for (Map.Entry<String, String> field : orFilter.entrySet()) {
        	queries.add(new Query(Criteria.where(field.getKey()).is(field.getValue())));
        }
		query.or(queries.toArray(new Query[0]));
	}


}
