package sk.r3n.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class SearchCriteria implements Serializable {

    private Integer start;

    private Integer count;

    private List<Sort> sort;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Sort> getSort() {
        if (sort == null) {
            sort = new ArrayList<>();
        }
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" + "start=" + start + ", count=" + count + ", sort=" + sort + '}';
    }
}
