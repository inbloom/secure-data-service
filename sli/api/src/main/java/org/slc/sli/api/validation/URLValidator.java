package org.slc.sli.api.validation;

import java.net.URI;

/**
 * Common interface for url validators
 * @author srupasinghe
 *
 */
public interface URLValidator {

    public boolean validate(URI url);
}
