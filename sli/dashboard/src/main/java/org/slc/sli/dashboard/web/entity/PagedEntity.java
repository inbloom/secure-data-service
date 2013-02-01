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


/**
 *
 */
package org.slc.sli.dashboard.web.entity;

/**
 * Pagination interface for various search forms.
 *
 * @author iivanisevic
 *
 */
public interface PagedEntity {
    /** default page being displayed, if not otherwise specified */
    public static final int DEFAULT_PAGE_NO = 1;
    /** default number of records per page, if not otherwise specified */
    public static final int DEFAULT_PAGE_SIZE = 50;

    /** @return the number of results displayed per page */
    public abstract int getPageSize();

    /** sets the number of results displayed per page */
    public abstract void setPageSize(int pageSize);

    /** @return the currently displayed page number */
    public abstract int getPageNumber();

    /** set the current page number to be displayed */
    public abstract void setPageNumber(int pageNo);
}
