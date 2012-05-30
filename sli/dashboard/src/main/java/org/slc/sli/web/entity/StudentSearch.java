package org.slc.sli.web.entity;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validatable student search form
 *
 * @author agrebneva
 *
 */
public class StudentSearch implements PagedEntity {
    @Size(max = 100, message = "Cannot exceed max size")
    @Pattern(regexp = "[a-zA-Z0-9-' ]*")
    private String firstName;

    @Size(max = 100, message = "Cannot exceed max size")
    @Pattern(regexp = "[a-zA-Z0-9-' ]*")
    private String lastName;

    private int pageNo = PagedEntity.DEFAULT_PAGE_NO;
    private int pageSize = PagedEntity.DEFAULT_PAGE_SIZE;

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
        return new String[] { firstName, lastName, String.valueOf(pageNo), String.valueOf(pageSize) };
    }

    @Override
    public void setPageNumber(int pageNo) {
        // invalid inputs will not change current settings
        if (pageNo > 0) {
            this.pageNo = pageNo;
        }
    }

    @Override
    public int getPageNumber() {
        return pageNo;
    }

    @Override
    public void setPageSize(int pageSize) {
        // invalid inputs will not change current settings
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
