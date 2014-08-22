package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchCriteria implements Serializable {

    public static Query criteriaToQuery(SearchCriteria searchCriteria, boolean count, Column... columns) {
        Query query = new Query();
        Query.Select select = query.SELECT(columns);
        select.count = count;
        select.firstRow = searchCriteria.firstRow;
        select.lastRow = searchCriteria.lastRow;
        select.distinct = searchCriteria.distinct;
        select.table = searchCriteria.table;
        select.joinCriteria = searchCriteria.joinCriteria;
        select.cm = searchCriteria.cm;
        select.orderCriteria = searchCriteria.orderCriteria;
        select.groupByColumns = searchCriteria.groupByColumns;
        return query;
    }

    protected int firstRow;

    protected int lastRow;

    protected boolean distinct;

    protected Table table;

    protected List<JoinCriterion> joinCriteria;

    protected CriteriaManager cm;

    protected List<OrderCriterion> orderCriteria;

    protected Column[] groupByColumns;

    public SearchCriteria() {
        cm = new CriteriaManager();
        joinCriteria = new ArrayList<JoinCriterion>();
        orderCriteria = new ArrayList<OrderCriterion>();
        distinct = false;
        firstRow = -1;
        lastRow = -1;
    }

    public void interval(int start, int count) {
        firstRow = 0;
        lastRow = 0;
        if (start < 0) {
            start = 0;
        }
        if (count < 0) {
            count = 0;
        }
        if (count != 0) {
            firstRow = start;
            lastRow = start + count;
        }
    }

    public void page(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        firstRow = page * size;
        lastRow = firstRow + size;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<JoinCriterion> getJoinCriteria() {
        return joinCriteria;
    }

    public CriteriaManager getCm() {
        return cm;
    }

    public List<OrderCriterion> getOrderCriteria() {
        return orderCriteria;
    }

    public void setGroupByColumns(Column... groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

}
