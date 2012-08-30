package org.slc.sli.api.security.service.mangler;

import java.util.Collection;

import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class QueryManglerFactory implements ApplicationContextAware {
    private Collection<Mangler> manglers;
    public Mangler getMangler(NeutralQuery query, SecurityCriteria criteria) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        manglers = applicationContext.getBeansOfType(Mangler.class).values();        
    }
}
