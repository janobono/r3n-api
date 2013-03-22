package sk.r3n.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DaoSearchCriteria {

    private int pageSize;

    private int pageNumber;

    private Map<SearchAttribute, Object[]> searchAttributes;

    private Map<SearchAttribute, SortOrder> sortAttributes;

    public DaoSearchCriteria() {
        super();
        searchAttributes = new HashMap<>();
        sortAttributes = new HashMap<>();
    }

    public void addSearchCriterium(final SearchAttribute searchAttribute, final SearchCondition searchCondition) {
        addSearchCriterium(searchAttribute, searchCondition, null);
    }

    public void addSearchCriterium(final SearchAttribute searchAttribute, final SearchCondition searchCondition,
            final Object value) {
        searchAttributes.put(searchAttribute, new Object[]{searchCondition, value});
    }

    public void addSortOrder(final SearchAttribute searchAttribute, final SortOrder sortOrder) {
        sortAttributes.put(searchAttribute, sortOrder);
    }

    public boolean containsCriteria() {
        return !searchAttributes.isEmpty();
    }

    public boolean containsSortOrder() {
        return !sortAttributes.isEmpty();
    }

    public Collection<SearchAttribute> getSearchCriterias() {
        return searchAttributes.keySet();
    }

    public Collection<SearchAttribute> getSortOrders() {
        return sortAttributes.keySet();
    }

    public SearchCondition getCriteriaCondition(final SearchAttribute searchAttribute) {
        return (SearchCondition) searchAttributes.get(searchAttribute)[0];
    }

    public Object getCriteriaValue(final SearchAttribute searchAttribute) {
        return searchAttributes.get(searchAttribute)[1];
    }

    public SortOrder getSortOrderValue(final SearchAttribute searchAttribute) {
        return sortAttributes.get(searchAttribute);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "DaoSearchCriteria{" + "pageSize=" + pageSize + ", pageNumber=" + pageNumber
                + ", searchAttributes=" + searchAttributes + ", sortAttributes=" + sortAttributes + '}';
    }
}
