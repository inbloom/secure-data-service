package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
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

    protected static Map<String, ComplexIdNormalizer> complexIdNormalizers = new HashMap<String, ComplexIdNormalizer>();
    static {
        complexIdNormalizers.put("studentTranscriptAssociation:body.studentId",
                new StudentTranscriptAssociationStudentIdComplexIdNormalizer());
    }

    /**
     * Resolves the specified field's reference and returns the associated ID. Returns an empty
     * list if ID cannot be resolved or if this class is not aware how to resolve that field. This
     * method
     * is for the non-standard resolvers that have custom definitions.
     *
     * @param entity
     *            entity containing field that needs to be resolved
     * @param field
     *            which field is currently being resolved
     * @param neutralQuery
     *            a query where "tenantId" is already specified
     * @param entityRepository
     *            access to execute query
     * @return resolved ID or an empty list
     */
    protected List<String> resolveComplexInternalId(Entity entity, String field, NeutralQuery neutralQuery)
            throws IdResolutionException {

        if (entity == null) {
            throw new IdResolutionException("Entity to resolve was null", field, null);
        }

        if (field == null) {
            throw new IdResolutionException("Field to resolve was null", null, null);
        }

        if (neutralQuery == null) {
            throw new IdResolutionException("NeutralQuery for ID resolution was null", field, null);
        }

        ComplexIdNormalizer complexIdNormalizer = complexIdNormalizers.get(entity.getType() + ":" + field);

        if (complexIdNormalizer == null) {
            throw new IdResolutionException("No defined complex resolver", field, null);
        } else {
            return complexIdNormalizer.resolveInternalId(entity, neutralQuery, this.entityRepository);
        }
    }

    public void resolveInternalIds(Entity entity, String tenantId, EntityConfig entityConfig, ErrorReport errorReport) {

        if (entityConfig.getReferences() == null) {
            return;
        }

        String resolvedReferences = "";

        try {
            for (RefDef reference : entityConfig.getReferences()) {

                int numRefInstances = getNumRefInstances(entity, reference.getRef());

                resolvedReferences += "       collectionName = " + reference.getRef().getCollectionName();

                for (List<Field> fields : reference.getRef().getChoiceOfFields()) {
                    for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                        for (Field field : fields) {
                            for (FieldValue fv : field.getValues()) {
                                if (fv.getRef() == null) {
                                    String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), reference.getRef(), refIndex);
                                    Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                                    if (entityValue != null) {
                                        if (!(entityValue instanceof Collection)) {
                                            resolvedReferences += ", value = " + entityValue.toString() + "\n";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                String fieldPath = reference.getFieldPath();

                List<String> ids = resolveReferenceInternalIds(entity, tenantId, reference.getRef(), fieldPath, errorReport);

                if (ids.size() == 0 && reference.getRef().isOptional()) {
                    return;
                }

                if (ids.size() != numRefInstances) {
                    LOG.error("Error in number of resolved internal ids");
                    String errorMessage = "ERROR: Failed to resolve expected number of references" + "\n"
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
                    for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {
                        String indexedFieldPath = fieldPath + ".[" + Integer.toString(refIndex) + "]";
                        PropertyUtils.setProperty(entity, indexedFieldPath, ids.get(refIndex));
                    }
                } else {
                    PropertyUtils.setProperty(entity, reference.getFieldPath(), ids.get(0));
                }

            }
        } catch (Exception e) {
            LOG.error("Error accessing property", e);
            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity   " + entity.getType()
                    + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                        + resolvedReferences;
            }

            errorReport.error(errorMessage, this);
        }
    }

    public String resolveInternalId(Entity entity, String tenantId, Ref refConfig, String fieldPath,
            ErrorReport errorReport, String resolvedReferences) {
        LOG.debug("resolving id for {}", entity);
        List<String> ids = resolveReferenceInternalIds(entity, tenantId, refConfig, fieldPath, errorReport);

        if (ids.size() == 0) {

            String errorMessage = "ERROR: Failed to resolve a reference" + "\n" + "       Entity   " + entity.getType()
                    + "\n" + "       Field    " + refConfig.getCollectionName() + "\n";

            if (resolvedReferences != null && !resolvedReferences.equals("")) {
                errorMessage += "     The failure can be identified with the following reference information: " + "\n"
                        + resolvedReferences;
            }

            errorReport.error(errorMessage, this);

            return null;
        }

        return ids.get(0);
    }

    private List<String> resolveReferenceInternalIds(Entity entity, String tenantId, Ref refConfig, String fieldPath,
            ErrorReport errorReport) {

        ProxyErrorReport proxyErrorReport = new ProxyErrorReport(errorReport);

        ArrayList<Query> queryOrList = new ArrayList<Query>();


        try {
            int numRefInstances = getNumRefInstances(entity, refConfig);

            //if the reference is a list of references loop over all elements adding an 'or' query statement for each
            for (List<Field> fields : refConfig.getChoiceOfFields()) {

                for (int refIndex = 0; refIndex < numRefInstances; ++refIndex) {

                    Query choice = new Query();

                    choice.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(
                        tenantId));

                    for (Field field : fields) {
                        List<Object> filterValues = new ArrayList<Object>();

                        for (FieldValue fv : field.getValues()) {
                            if (fv.getRef() != null) {
                                filterValues.addAll(resolveReferenceInternalIds(entity, tenantId, fv.getRef(), fieldPath,
                                        proxyErrorReport));
                            } else {
                                String valueSourcePath = constructIndexedPropertyName(fv.getValueSource(), refConfig, refIndex);
                                Object entityValue = PropertyUtils.getProperty(entity, valueSourcePath);
                                if (entityValue instanceof Collection) {
                                    Collection<?> entityValues = (Collection<?>) entityValue;
                                    filterValues.addAll(entityValues);
                                } else if (entityValue == null) {
                                    NeutralQuery neutralQuery = new NeutralQuery();
                                    String tenantKey = METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey();
                                    neutralQuery.addCriteria(new NeutralCriteria(tenantKey, "=", tenantId, false));
                                    return this.resolveComplexInternalId(entity, fieldPath, neutralQuery);
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
            if (refConfig.isOptional()) {
                return new ArrayList<String>();
            }
            LOG.error("Error accessing property", e);
            proxyErrorReport.error("Failed to resolve a reference (3).", this);
        }

        if (proxyErrorReport.hasErrors()) {
            return null;
        }

        //combine the queries with or (must be done this way because Query.or overrides itself)
        Query filter = new Query();
        filter.or(queryOrList.toArray(new Query[queryOrList.size()]));

        @SuppressWarnings("deprecation")
        Iterable<Entity> foundRecords = entityRepository.findByQuery(refConfig.getCollectionName(), filter, 0, 0);

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

        return result;
    }
}

