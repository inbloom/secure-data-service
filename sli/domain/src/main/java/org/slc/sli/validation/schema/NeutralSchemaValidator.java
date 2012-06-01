package org.slc.sli.validation.schema;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;

/**
 * Validates an Entity body against the appropriate SLI neutral schema.
 *
 * @author Sean Melody <smelody@wgen.net>
 * @author Ryan Farris <rfarris@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Component
public class NeutralSchemaValidator implements EntityValidator {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(NeutralSchemaValidator.class);

    // Attributes
    @Autowired
    private SchemaRepository entitySchemaRegistry;
    
    @Autowired
    private Repository<Entity> validationRepo;
    
    // Constructors
    public NeutralSchemaValidator() {

    }

    public NeutralSchemaValidator(SchemaRepository entitySchemaRegistry) {
        this.entitySchemaRegistry = entitySchemaRegistry;
    }

    // Methods

    /**
     * Validates the given entity using its SLI Neutral Schema.
     */
    @Override
    public boolean validate(Entity entity) throws EntityValidationException {

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema == null) {
            LOG.warn("No schema associatiated for type {}", entity.getType());
            return true;
        }

        List<ValidationError> errors = new LinkedList<ValidationError>();
        boolean valid = schema.validate("", entity.getBody(), errors, validationRepo);
        if (!valid) {
            LOG.debug("Errors detected in {}, {}", new Object[]{entity.getEntityId(), errors});
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
        }

        return true;
    }

    public void setSchemaRegistry(SchemaRepository schemaRegistry) {
        entitySchemaRegistry = schemaRegistry;
    }
    
    public void setEntityRepository(Repository<Entity> entityRepo) {
        this.validationRepo = entityRepo;
    }

}
