package org.slc.sli.web.entity;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validatable UUID string to be accepted as a modelattribute, requestparam, or pathvariable
 * @author agrebneva
 *
 */
public class SafeUUID {
    @Size(max = 36, message = "Not a valid UUID")
    @Pattern(regexp = "[A-Za-z0-9-]{50}")
    String uuid;

    public SafeUUID() {
    }

    public SafeUUID(String id) {
        setId(id);
    }

    public String getId() {
        return uuid;
    }

    public void setId(String uuid) {
            this.uuid = uuid;
    }
}
