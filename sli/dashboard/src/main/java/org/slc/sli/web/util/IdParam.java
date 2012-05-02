package org.slc.sli.web.util;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Represents verifiable client input
 * @author agrebneva
 *
 */
public class IdParam {
    @Size(max = 64)
    @Pattern(regexp = "[a-zA-z0-9-]")
    String id;
}
