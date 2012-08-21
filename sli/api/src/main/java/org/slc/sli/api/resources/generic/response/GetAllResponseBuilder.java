package org.slc.sli.api.resources.generic.response;

import org.slc.sli.api.representation.EntityBody;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds a get all (one part) response
 *
 * @author srupasinghe
 */

@Scope("request")
@Component
public class GetAllResponseBuilder extends GetResponseBuilder {

    @Override
    protected Object getResponseObject(List<EntityBody> entities) {
        return entities;
    }
}
