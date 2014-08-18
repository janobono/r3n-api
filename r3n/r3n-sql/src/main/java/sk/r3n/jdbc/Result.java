package sk.r3n.jdbc;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result<T> implements Serializable {

    private int pageSize;

    private List<T> results;

    private int totalRowsCount;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getResults() {
        if (results == null) {
            results = new ArrayList<T>();
        }
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getTotalRowsCount() {
        return totalRowsCount;
    }

    public void setTotalRowsCount(int totalRowsCount) {
        this.totalRowsCount = totalRowsCount;
    }

    public int getTotalPages() {
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
        return "Result{" + "pageSize=" + pageSize + ", results=" + results + ", totalRowsCount=" + totalRowsCount + '}';
    }
}
