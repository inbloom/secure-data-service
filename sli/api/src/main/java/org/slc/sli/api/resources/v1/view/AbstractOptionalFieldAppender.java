package org.slc.sli.api.resources.v1.view;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.config.EntityDefinitionStore;

/**
 * Base class for all the strategy implementations for the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
public abstract class AbstractOptionalFieldAppender implements OptionalFieldAppender {
    
    @Autowired
    protected EntityDefinitionStore entityDefs;
    
    public AbstractOptionalFieldAppender() {
    }
}
