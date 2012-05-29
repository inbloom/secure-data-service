package org.slc.sli.api.security.context.resolver;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

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
        // Only include allow-all resolver for specific entities that are supposed to be completely public
        return EntityNames.USER_ACCOUNT.equals(toEntityType) || EntityNames.WAITING_LIST_USER_ACCOUNT.equals(toEntityType);
    }
}
