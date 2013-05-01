package sk.r3n.jdbc.query;

/**
 *
 * @author jan
 */
public interface QueryAttribute {
    
    public String name();
    
    public QueryTable table();
    
    public String nameWithAlias();
    
}
