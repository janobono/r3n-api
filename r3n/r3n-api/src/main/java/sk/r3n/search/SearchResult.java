package sk.r3n.search;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {

    private final List<T> results;

    private final int totalPages;

    private final int totalRowsCount;

    private SearchResult(Builder builder) {
        if (builder.results == null) {
            throw new NullPointerException("Search result is null.");
        }

        this.results = builder.results;
        this.totalRowsCount = builder.totalRowsCount;

        int numOfAllPages = builder.totalPages;

        if (builder.pageSize != null) {
            if (builder.totalRowsCount <= builder.pageSize
                    && builder.totalRowsCount > 0) {
                numOfAllPages = 1;
            }

            if (builder.totalRowsCount > builder.pageSize) {
                int pageOfset = builder.totalRowsCount % builder.pageSize == 0 ? 0 : 1;
                numOfAllPages = builder.totalRowsCount / builder.pageSize + pageOfset;
            }
        }

        this.totalPages = numOfAllPages;
    }

    public List<T> getResults() {
        return results;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getTotalRowsCount() {
        return totalRowsCount;
    }

    public static class Builder<T> {

        private List<T> results;

        private Integer totalPages;

        private Integer totalRowsCount;

        private Integer pageSize;

        public Builder setResults(List<T> results) {
            this.results = results;
            return this;
        }

        public Builder setTotalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder setTotalRowsCount(int totalRowsCount) {
            this.totalRowsCount = totalRowsCount;
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public SearchResult build() {
            return new SearchResult(this);
        }
    }
}
