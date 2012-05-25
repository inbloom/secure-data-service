package org.slc.sli.validation;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides a reference to the neutral schema repository
 *
 * @author srupasinghe
 *
 */
@Component
public class NeutralSchemaRepositoryProvider implements SchemaRepositoryProvider, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public SchemaRepository getSchemaRepository() {
        return applicationContext.getBean(SchemaRepository.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
