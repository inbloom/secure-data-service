/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        for(Mangler mangler : manglers) {
            if (mangler.respondsTo(criteria.getCollectionName())) {
                mangler.setTheQuery(query);
                mangler.setSecurityCriteria(criteria.getSecurityCriteria());
                return mangler;
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        manglers = applicationContext.getBeansOfType(Mangler.class).values();
    }
}
