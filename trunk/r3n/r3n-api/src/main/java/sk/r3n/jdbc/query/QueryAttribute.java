package sk.r3n.jdbc.query;

public interface QueryAttribute {
    
    public String name();
    
    public QueryTable table();
    
    public String nameWithAlias();
    
    public DataType dataType();
    
}
