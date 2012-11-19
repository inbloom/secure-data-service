/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private String firstName;

    @Size(max = 100, message = "Cannot exceed max size")
    @Pattern(regexp = "[a-zA-Z0-9-' ]*")
    private String lastName;

    @Pattern(regexp = "[a-fA-F0-9\\-]*_id")
    private String schoolId;

    private int pageNo = PagedEntity.DEFAULT_PAGE_NO;
    private int pageSize = PagedEntity.DEFAULT_PAGE_SIZE;

    public StudentSearch() {
    }

    public StudentSearch(String firstName, String lastName, String schoolId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
    }

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

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String[] get() {
        return new String[] { this.firstName, this.lastName, String.valueOf(this.pageNo),
                String.valueOf(this.pageSize), this.schoolId };
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
