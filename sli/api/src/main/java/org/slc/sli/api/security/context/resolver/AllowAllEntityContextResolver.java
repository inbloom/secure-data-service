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
