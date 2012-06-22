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


package org.slc.sli.api.security.context.resolver;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Default context traversing implementation to deny access to everything
 */
@Component
public class DenyAllContextResolver implements EntityContextResolver {
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return false;
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        return Collections.emptyList();
    }
}
