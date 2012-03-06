package org.slc.sli.api.security.context;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * TeacherAttendanceContextResolver
 * Determine security authorization.
 * Finds the Attendance records a user has context to access.
 */
@Component
public class SectionSessionContextResolver implements EntityContextResolver {

    @Autowired
    private EntityRepository repository;
    @Autowired
    private EntityDefinitionStore definitionStore;

    @Override
    public String getSourceType() {
        return EntityNames.SECTION;
    }

    @Override
    public String getTargetType() {
        return EntityNames.SESSION;
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        return Arrays.asList((String) principal.getBody().get("sessionId"));
    }

    public void setRepository(EntityRepository repository) {
        this.repository = repository;
    }

    public void setDefinitionStore(EntityDefinitionStore definitionStore) {
        this.definitionStore = definitionStore;
    }
}
