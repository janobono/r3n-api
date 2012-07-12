package sk.r3n.db;

public interface SQLGenerator extends Condition, Operator {

	public String putIntoQuotes(String string);

	public String toSQL(boolean tablePrefix, SQLColumn column, boolean asc);

	public String toSQL(boolean tablePrefix, SQLCondition condition);

	public String toSQL(boolean tablePrefix, SQLCondition[] conditions);

	public String toSQL(boolean tablePrefix, SQLCondition[][] conditions);

	// DELETE---------------------------------------------------------------------------
	public String delete(String table);

	public String delete(String table, SQLCondition condition);

	public String delete(String table, SQLCondition[] conditions);

	public String delete(String table, SQLCondition[][] conditions);

	// INSERT---------------------------------------------------------------------------
	public String insert(String table, SQLColumn[] columns);

	// SELECT---------------------------------------------------------------------------
	public String select(SQLColumn[] columns, String table,
			SQLCondition condition);

	public String select(SQLColumn[] columns, String table,
			SQLCondition[] conditions);

	public String select(SQLColumn[] columns, String table, SQLColumn orderBy,
			boolean asc);

	public String select(SQLColumn[] columns, String table,
			SQLCondition condition, SQLColumn orderBy, boolean asc);

	public String select(SQLColumn[] columns, String table,
			SQLCondition[] conditions, SQLColumn orderBy, boolean asc);

	public String select(boolean distinct, SQLColumn[] columns, String table,
			SQLColumn orderBy, boolean asc);

	public String select(boolean distinct, SQLColumn[] columns, String table,
			SQLCondition[] conditions, SQLColumn orderBy, boolean asc);

	public String select(boolean distinct, SQLColumn[] columns, String table,
			SQLCondition[][] conditions, SQLColumn orderBy, boolean asc);

	public String selectCount(String table);

	public String selectCount(String table, SQLCondition condition);

	public String selectCount(String table, SQLCondition[] conditions);

	public String selectCount(String table, SQLCondition[][] conditions);

	public String selectMax(SQLColumn column, String table);

	public String selectMax(SQLColumn column, String table,
			SQLCondition condition);

	public String selectMax(SQLColumn column, String table,
			SQLCondition[] conditions);

	public String selectMax(SQLColumn column, String table,
			SQLCondition[][] conditions);

	// UPDATE---------------------------------------------------------------------------
	public String update(String table, SQLColumn[] columns);

	public String update(String table, SQLColumn[] columns,
			SQLCondition condition);

	public String update(String table, SQLColumn[] columns,
			SQLCondition[] conditions);

	public String update(String table, SQLColumn[] columns,
			SQLCondition[][] conditions) ;

	// UNION----------------------------------------------------------------------------
	public String union(String[] selects, Integer orderBy);
}
