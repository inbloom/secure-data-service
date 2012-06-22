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

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

/**
 * Default context traversing implementation that allows access to everything
 */
@Component
public class AllowAllEntityContextResolver implements EntityContextResolver {
    /**
     * List that always says 'YES' I have it
     */
    public static final List<String> SUPER_LIST = new AbstractList<String>() {

        @Override
        public boolean contains(Object obj) {
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return true;
        }

        @Override
        public String get(int index) {
            return "";
        }

        @Override
        public int size() {
            return -1;
        }

        @Override
        public String toString() {
            return "SUPER LIST";
        }

    };

    @Override
    public List<String> findAccessible(Entity principal) {
        return SUPER_LIST;
    }

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return (toEntityType.equals(EntityNames.LEARNINGOBJECTIVE) || toEntityType.equals(EntityNames.LEARNINGSTANDARD));
    }
}
