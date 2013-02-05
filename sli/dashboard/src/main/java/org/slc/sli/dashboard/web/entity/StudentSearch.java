/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
