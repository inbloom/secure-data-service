/**
 *
 */
package org.slc.sli.web.entity;

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
