package org.slc.sli.api.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.ReferenceSchema;

/**
 * Definition of an entity resource
 *
 * @author nbrown
 *
 */
public class EntityDefinition {

    private final String type;
    private final String resourceName;
    private final EntityService service;
    private final String collectionName;
    private final List<AssociationDefinition> linkedAssociations;
    private static EntityRepository defaultRepo;
    private NeutralSchema schema;
    private LinkedHashMap<String, ReferenceSchema> referenceFields;

    protected EntityDefinition(String type, String resourceName, String collectionName, EntityService service) {
        this.type = type;
        this.resourceName = resourceName;
        this.collectionName = collectionName;
        this.service = service;
        this.linkedAssociations = new LinkedList<AssociationDefinition>();
    }
    
    /**
     * Associates a schema to an entity definition. This also has a side effect of scanning the fields for any reference fields and recording them
     * for later access via "getReferenceFields()".
     * 
     * 
     * @param neutralSchema schema that can identify a valid instance of this entity type
     */
    public void setSchema(NeutralSchema neutralSchema) {
        //store reference
        this.schema = neutralSchema;
        
        //create separate map just for reference fields
        this.referenceFields = new LinkedHashMap<String, ReferenceSchema>();
        
        //confirm schema was loaded
        if (this.schema != null) {
            //loop through all fields
            for (Map.Entry<String, NeutralSchema> entry : this.schema.getFields().entrySet()) {
                //if field is a reference field 
                if (entry.getValue() instanceof ReferenceSchema) {
                    //put field name and collection referenced
                    this.referenceFields.put(entry.getKey(), (ReferenceSchema) entry.getValue());
                }
            }
        }
    }
    
    /**
     * Returns a map of all fields that are references from the field name to the collection referenced.
     * 
     * @return map of field names to collections referenced
     */
    public final Map<String, ReferenceSchema> getReferenceFields() {
        return this.referenceFields;
    }

    public final void addLinkedAssoc(AssociationDefinition assocDefn) {
        this.linkedAssociations.add(assocDefn);
    }

    public final Collection<AssociationDefinition> getLinkedAssoc() {
        return this.linkedAssociations;
    }

    public String getStoredCollectionName() {
        return this.collectionName;
    }

    public String getType() {
        return type;
    }

    /**
     * The name of the resource name in the ReST URI
     *
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }

    public EntityService getService() {
        return service;
    }

    public boolean isOfType(String id) {
        return service.exists(id);
    }

    public static void setDefaultRepo(EntityRepository defaultRepo) {
        EntityDefinition.defaultRepo = defaultRepo;
    }

    public static EntityRepository getDefaultRepo() {
        return defaultRepo;
    }

}
