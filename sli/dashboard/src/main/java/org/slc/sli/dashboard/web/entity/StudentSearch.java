package org.slc.sli.dashboard.web.entity;

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
    private String name;

    private int pageNo = PagedEntity.DEFAULT_PAGE_NO;
    private int pageSize = PagedEntity.DEFAULT_PAGE_SIZE;

    public StudentSearch() {
        //Default constructor
    }

    public StudentSearch(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] get() {
        return new String[] { this.name, String.valueOf(this.pageNo),
                String.valueOf(this.pageSize) };
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
