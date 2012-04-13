package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
                resolvedReferences += getResolvedRefString(entity, reference.getRef());

                List<String> ids = resolveReferenceInternalIds(entity, tenantId, reference.getRef(), errorReport);

                int numRefInstances = getNumRefInstances(entity, reference.getRef());
                if (ids.size() != numRefInstances) {
                    LOG.error("Error in number of resolved internal ids");
                    String errorMessage = "ERROR: ERROR: Failed to resolve expected number of references" + "\n"
                                        + "       Entity   " + entity.getType() + "\n";

                    if (resolvedReferences != null && !resolvedReferences.equals("")) {
                        errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                                      + resolvedReferences;
                    }

                    errorReport.error(errorMessage, this);
                }
                
                if (errorReport.hasErrors()) {
                    return;
                }
               

                if (reference.getRef().isRefList()) {
                    //for lists of references set the properties on each element of the resolved ID list
                    for(int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                        String fieldPath = reference.getFieldPath() + ".[" + Integer.toString(refIndex)+ "]";
                        PropertyUtils.setProperty(entity, fieldPath, ids.get(refIndex));
                    }
                } else {
                    PropertyUtils.setProperty(entity, reference.getFieldPath(), ids.get(0));
                }
                
            }
        } catch (Exception e) {
            LOG.error("Error accessing property", e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n"
                                + "       Entity   " + entity.getType() + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals("")) {
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

            if (resolvedReferences != null && !resolvedReferences.equals("")) {
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

        ArrayList<Query> queryOrList = new ArrayList<Query>(); 
        

        try {
            int numRefInstances = getNumRefInstances(entity, refConfig);
            
            //if the reference is a list of references loop over all elements adding an 'or' query statement for each
            for (List<Field> fields : refConfig.getChoiceOfFields()) {
                
                for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                    
                    Query choice = new Query();
                    choice.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(tenantId));
                    
                    for (Field field : fields) {
                        List<Object> filterValues = new ArrayList<Object>();

                        for (FieldValue fv : field.getValues()) {
                            if (fv.getRef() != null) {
                                filterValues.addAll(resolveReferenceInternalIds(entity, tenantId, fv.getRef(), proxyErrorReport));
                            } else {
                                String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), refConfig, refIndex);
                                Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                                
                                //TODO this doesn't take account of lists of references that are not within the top level
                                //will not be able to use PropertyUtils.getProperty for arbitrary nested lists
                                //need to implement a recursive list/map/string get method
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

                    queryOrList.add(choice);
                }
            }
        } catch (Exception e) {
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference", this);
        }

        if (proxyErrorReport.hasErrors()) {
            return null;
        }

        //combine the queries with or (must be done this way because Query.or overrides itself)
        Query filter = new Query();
        filter.or(queryOrList.toArray(new Query[queryOrList.size()]));
        
        @SuppressWarnings("deprecation")
        Iterable<Entity> foundRecords = entityRepository.findByQuery(collection, filter, 0, 0);
        
        List<String> ids = new ArrayList<String>();

        if (foundRecords != null && foundRecords.iterator().hasNext()) {
            for (Entity record : foundRecords) {
                Map<String, Object> body = record.getBody();
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
    
    /**
     * Returns the number of reference instances of a Ref object in a given entity
     */
    private int getNumRefInstances(Entity entity, Ref refConfig) throws Exception {
        int numRefInstances = 1;
        if (refConfig.isRefList()) {
            List<?> refValues = (List<?>) PropertyUtils.getProperty(entity, refConfig.getRefObjectPath());
            numRefInstances = refValues.size();
        }
        return numRefInstances;
    }

    /**
     * Constructs the property name used by PropertyUtils.getProperty for indexed references 
     */
    private String constructIndexedPropertyName(String valueSource, Ref refConfig, int refIndex) {
        String result = valueSource;
        
        if (refConfig.isRefList()) {
            result = "";
            String refObjectPath = refConfig.getRefObjectPath();
            //split the valueSource by .
            StringTokenizer tokenizer = new StringTokenizer(valueSource, ".");
            while(tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                result += token;
                if (result.equals(refObjectPath)) {
                    result += ".[" + Integer.toString(refIndex) + "]";
                }
                if( tokenizer.hasMoreTokens() ) {
                    result += ".";
                }
            }
        }
        
        return result;
    }
   
    /*
     * Returns a string listing all resolved references in the specified entity
     */
    private String getResolvedRefString(Entity entity, Ref refConfig) throws Exception{
        String resolvedRefs = "";
        int numRefInstances = getNumRefInstances(entity, refConfig);
        for (List<Field> fields : refConfig.getChoiceOfFields()) {
            for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                for (Field field : fields) { 
                    for (FieldValue fv : field.getValues()) {
                        if (fv.getRef() == null) {
                            String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), refConfig, refIndex);
                            Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                            if (!(entityValue instanceof Collection)) {
                                resolvedRefs += ", value = " + entityValue.toString() + "\n";
                            }
                        }
                    }
                }
            }
        }
        return resolvedRefs;
    }

}
