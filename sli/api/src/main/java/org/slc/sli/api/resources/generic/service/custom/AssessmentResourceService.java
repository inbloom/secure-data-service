package org.slc.sli.api.resources.generic.service.custom;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;

import java.net.URI;

/**
 * Assessment Resource service.
 *
 * @author srupasinghe
 *
 */

public interface AssessmentResourceService {

    public ServiceResponse getLearningStandards(final Resource base, final String idList, final Resource returnResource, final URI requestURI);

}
