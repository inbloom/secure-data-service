package org.slc.sli.ingestion.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.cache.CacheProvider;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Matches external / natural IDs to IDs in the database.
 *
 * @author ablum
 *
 */
@Component
public class InternalIdNormalizer {

    private static final String METADATA_BLOCK = "metaData";

    private static final Logger LOG = LoggerFactory.getLogger( InternalIdNormalizer.class);

    private static final String CACHE_NAMESPACE = "oldId";

    @Autowired
    private CacheProvider cacheProvider;



    /**
     * Resolve references defined by external IDs (from clients) with internal IDs from SLI data
     * store. Uses a multiple search criteria to resolve the reference
     *
     * @param collection
     *            Referenced collection
     * @param tenantId
     *            ID namespace that uniquely identifies external ID
     * @param externalSearchCriteria
     *            Search criteria that is used to resolve an externalId
     * @param errorReport
     *            Error reporting
     * @return The resolved internalId
     */
    public String resolveInternalId(Repository<Entity> entityRepository, String collection, String tenantId, Map<?, ?> externalSearchCriteria, ErrorReport errorReport) {

        String cached = getFromCache(collection, tenantId, String.valueOf(externalSearchCriteria.hashCode()));
        if (cached != null) {
            return cached;
        }

        Map<String, String> filterFields = new HashMap<String, String>();
        filterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), tenantId);

        Query query = new Query();
        resolveSearchCriteria(entityRepository, collection, filterFields, externalSearchCriteria, query, tenantId, errorReport);
        query.fields().include("_id");

        Iterable<Entity> found = entityRepository.findByQuery(collection, query, 0, 1);

        if (found == null || !found.iterator().hasNext()) {
            errorReport.error("Cannot find [" + collection + "] record using the following filter: " + query.getQueryObject().toString(), InternalIdNormalizer.class);

            return null;
        }

        Entity entity = found.iterator().next();

        cache(collection,tenantId,String.valueOf(externalSearchCriteria.hashCode()),entity.getEntityId());

        return entity.getEntityId();
    }

    /**
     * Resolve references defined by external IDs (from clients) with internal IDs from SLI data
     * store.
     *
     * @param collection
     *            Referenced collection
     * @param tenantId
     *            ID namespace that uniquely identifies external ID
     * @param externalId
     *            External ID to be resolved
     * @param errorReport
     *            Error reporting
     * @return Resolved internal ID
     */
    public String resolveInternalId(Repository<Entity> entityRepository, String collection, String tenantId, String externalId, ErrorReport errorReport) {

        String cached = getFromCache(collection, tenantId, externalId);
        if (cached != null) {
            return cached;
        }

        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), "=", tenantId, false));
        nq.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), "=", externalId, false));
        nq.setIncludeFields("_id");

        Entity e = entityRepository.findOne(collection, nq);
        LOG.debug("~Entity~ {}", e == null ? "Not Found" : e.getType());
        if (e == null) {
            errorReport.error("Cannot find [" + collection + "] record using the following filter: " + nq, InternalIdNormalizer.class);

            return null;
        }

        cache(collection,tenantId,externalId,e.getEntityId());
        return e.getEntityId();
    }

    /**
     * Adds the criteria that searches within the same collection to the query
     *
     * @param collection
     * @param filterFields
     * @param externalSearchCriteria
     * @param query
     */
    private void resolveSearchCriteria(Repository<Entity> entityRepository, String collection, Map<String, String> filterFields, Map<?, ?> externalSearchCriteria, Query query, String tenantId, ErrorReport errorReport) {
        for (Map.Entry<?, ?> searchCriteriaEntry : externalSearchCriteria.entrySet()) {

            StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
            String pathCollection = tokenizer.nextToken();
            pathCollection = WordUtils.uncapitalize(pathCollection);

            if (pathCollection.equals(collection) && searchCriteriaEntry.getValue() != null) {

                resolveSameCollectionCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());
                addSearchPathsToQuery(query, filterFields);

            } else {

                resolveDifferentCollectionCriteria(entityRepository, query, searchCriteriaEntry, tenantId, errorReport);

            }
        }

    }

    /**
     * Recursively traverses a complex reference and adds all mongo path and value pairs to the
     * query filter
     *
     * @param filterFields
     *            Mongo path and value pairs that are filtered in the query
     * @param key
     * @param value
     */
    private void resolveSameCollectionCriteria(Map<String, String> filterFields, String key, Object value) {
        if (String.class.isInstance(value)) {

            StringTokenizer tokenizer = new StringTokenizer(key, "#");
            tokenizer.nextToken();
            String newPath = tokenizer.nextToken();
            filterFields.put(newPath, value.toString());

        } else if (Map.class.isInstance(value)) {

            for (Map.Entry<?, ?> searchCriteriaEntry : ((Map<?, ?>) value).entrySet()) {

                resolveSameCollectionCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());

            }

        } else if (List.class.isInstance(value)) {

            for (Object object : (List<?>) value) {

                resolveSameCollectionCriteria(filterFields, key, object);

            }

        }

    }

    /**
     * Adds the criteria that searches within a different collection to the query
     *
     * @param query
     * @param externalSearchCriteria
     * @param errorReport
     */
    private void resolveDifferentCollectionCriteria(Repository<Entity> entityRepository, Query query, Map.Entry<?, ?> searchCriteriaEntry, String tenantId, ErrorReport errorReport) {
        StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
        String pathCollection = tokenizer.nextToken();
        pathCollection = WordUtils.uncapitalize(pathCollection);
        String referencePath = tokenizer.nextToken();

        Map<String, String> tempFilter = new HashMap<String, String>();
        Query referenceQuery = new Query();
        if (searchCriteriaEntry.getValue() instanceof String) {
            Map<String, String> searchCriteriaEntryMap = new HashMap<String, String>();
            searchCriteriaEntryMap.put((String) searchCriteriaEntry.getKey(), (String) searchCriteriaEntry.getValue());
            resolveSearchCriteria(entityRepository, pathCollection, tempFilter, searchCriteriaEntryMap, referenceQuery, tenantId, errorReport);
        } else {
            resolveSearchCriteria(entityRepository, pathCollection, tempFilter, (Map<?, ?>) searchCriteriaEntry.getValue(), referenceQuery, tenantId, errorReport);
        }

        if (tempFilter.isEmpty()) {

            return;
        }
        tempFilter.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), tenantId);
        Iterable<Entity> referenceFound = entityRepository.findByPaths(pathCollection, tempFilter);

        if (referenceFound == null || !referenceFound.iterator().hasNext()) {
            errorReport.error("Cannot find [" + pathCollection + "] record using the following filter: " + tempFilter.toString(), InternalIdNormalizer.class);
        } else {

            Map<String, String> orFilter = new HashMap<String, String>();

            for (Entity found : referenceFound) {

                orFilter.put(referencePath, found.getEntityId());

            }

            addOrToQuery(query, orFilter);
        }

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


    //  Caching POC

    private String getFromCache(String collection, String tenantId, String criteria) {

        String key = composeKey(collection, tenantId, criteria);
        String found = (String) cacheProvider.get( key );


        return found;
    }

    private void cache(String collection, String tenantId, String criteria, String value) {
        String key = composeKey(collection, tenantId, criteria);

        cacheProvider.add( key, value );
    }

    private String composeKey(String collection, String tenantId, String criteria) {
        return String.format("%s_%s_%s_%s", CACHE_NAMESPACE, collection, tenantId, criteria);
    }

}
