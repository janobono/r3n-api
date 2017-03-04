/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Common search result.
 *
 * @param <T>
 */
public class SearchResult<T> implements Serializable {

    private Integer pageSize;

    private List<T> results;

    private Integer totalRowsCount;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getResults() {
        if (results == null) {
            results = new ArrayList<>();
        }
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Integer getTotalRowsCount() {
        return totalRowsCount;
    }

    public void setTotalRowsCount(Integer totalRowsCount) {
        this.totalRowsCount = totalRowsCount;
    }

    public Integer getTotalPages() {
        if (totalRowsCount <= 0 || pageSize <= 0) {
            return 0;
        }
        if (totalRowsCount <= pageSize) {
            return 1;
        }
        int result = totalRowsCount / pageSize;
        if (totalRowsCount % pageSize > 0) {
            result++;
        }
        return result;
    }

    @Override
    public String toString() {
        return "SearchResult{" + "pageSize=" + pageSize + ", results=" + results + ", totalRowsCount=" + totalRowsCount + '}';
    }
}
