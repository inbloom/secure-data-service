package org.slc.sli.web.entity;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validatable student search form
 * @author agrebneva
 *
 */
public class StudentSearch {
    @Size(max = 100, message = "Cannot exceed max size")
    @Pattern(regexp = "[a-zA-Z0-9-' ]*")
    private String firstName;

    @Size(max = 100, message = "Cannot exceed max size")
    @Pattern(regexp = "[a-zA-Z0-9-' ]*")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public StudentSearch() {
    }

    public StudentSearch(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String[] get() {
        return new String[]{firstName, lastName};
    }
}
