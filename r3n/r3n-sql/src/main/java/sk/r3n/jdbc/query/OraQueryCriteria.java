package sk.r3n.jdbc.query;

/**
 *
 * @author jan
 */
public class OraQueryCriteria extends QueryCriteria {

    @Override
    public void setInterval(int start, int count) {
        this.firstRow = 0;
        this.lastRow = 0;
        if (start < 0) {
            start = 0;
        }
        if (count < 0) {
            count = 0;
        }
        if (count != 0) {
            this.firstRow = start + 1;
            this.lastRow = start + count;
        }
    }

    @Override
    public void setPage(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        this.firstRow = page * size + 1;
        this.lastRow = firstRow + size;
    }

    @Override
    public int getPageSize() {
        return (getLastRow() + 1) - getFirstRow();
    }

}
