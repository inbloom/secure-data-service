package org.slc.sli.validation.schema;

import java.util.List;

import org.slc.sli.validation.SchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * SLI reference schema that validates entity references
 *
 * @author srupasinghe
 *
 */
@Scope("prototype")
@Component
public class ReferenceSchema extends NeutralSchema  {
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceSchema.class);

    private SchemaRepository schemaRepository;

    // Constructors
    public ReferenceSchema() {
        this(NeutralSchemaType.REFERENCE.getName());
    }

    public ReferenceSchema(String xsdType) {
        super(xsdType);
    }

    public ReferenceSchema(String xsdType, SchemaRepository schemaRepository) {
        super(xsdType);
        this.schemaRepository = schemaRepository;
    }

    // Methods
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.REFERENCE;
    }

    /**
     * Identifies what resource this schema references.
     *
     * @return a collection/resource name this reference refers to
     */
    public String getResourceName() {
        return getCollectionType();
    }

    @Override
    public Object convert(Object value) {
        return value;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        if (!addError(String.class.isInstance(entity), fieldName, entity, "String", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }

        boolean found = false;
        String collectionType = getCollectionType();

        try {
            // try to find an entity with the given id
            found = repo.exists(collectionType, (String) entity);
        } catch (Exception e) {
            // repo.find is currently throwing multiple kinds of exceptions so we will catch all for
            // now, as we sort out what is thrown and why
            LOG.debug("Exception when looking up reference in repository. ", e);
        }

        // if not found add the appropriate error
        if (!found) {
            LOG.debug("Broken reference in {}, {}, {}", new Object[] { entity, fieldName, errors });

            addError(false, fieldName, entity, "Valid reference", ErrorType.REFERENTIAL_INFO_MISSING, errors);
            return false;
        }

        return true;
    }

    private String getCollectionType() {
        String collectionType = getAppInfo().getReferenceType();

        if (schemaRepository != null) {
            //get the reference schema
            NeutralSchema referenceSchema = schemaRepository.getSchema(collectionType);

            //get the collection type
            if (referenceSchema.getAppInfo() != null && referenceSchema.getAppInfo().getCollectionType() != null) {
                collectionType = referenceSchema.getAppInfo().getCollectionType();
            }
        }

        return collectionType;
    }


}
