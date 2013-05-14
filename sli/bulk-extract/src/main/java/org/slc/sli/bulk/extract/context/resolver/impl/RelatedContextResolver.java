/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.bulk.extract.context.resolver.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;

/**
 * Resolver for entities directly related to another entity
 * 
 * @author nbrown
 * 
 */
public abstract class RelatedContextResolver implements ContextResolver {
    
    public RelatedContextResolver() {
        super();
    }
    
    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        if (entity.getBody() == null) {
            return Collections.emptySet();
        }
        
        String referredId = getReferredId(entity.getType(), entity.getBody());
        if (referredId == null) {
            return Collections.emptySet();
        }
        
        return getReferredResolver().findGoverningLEA(referredId);
    }
    
    protected String getReferredId(String type, Map<String, Object> body) {
        String reference = getReferenceProperty(type);
        if (reference == null) {
            return null;
        }
        String referredId = (String) body.get(reference);
        return referredId;
    }
    
    protected abstract String getReferenceProperty(String entityType);
    
    protected abstract ReferrableResolver getReferredResolver();
    
}