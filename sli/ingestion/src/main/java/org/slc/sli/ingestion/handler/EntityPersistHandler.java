package org.slc.sli.ingestion.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Handles the persisting of Entity objects
 *
 * @author dduran
 *
 */
@Component
public class EntityPersistHandler extends AbstractIngestionHandler<Entity, Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(EntityPersistHandler.class);

    private EntityRepository entityRepository;

    @Override
    Entity doHandling(Entity entity, ErrorReport errorReport) {
        return entityRepository.create(entity.getType(), entity.getBody());
    }

    public void setEntityRepository(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

}
