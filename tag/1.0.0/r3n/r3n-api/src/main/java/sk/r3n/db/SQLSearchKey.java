package sk.r3n.db;

public class SQLSearchKey {
	
	private SQLCondition sqlCondition;
	
	private Object value;

	public SQLSearchKey(SQLCondition sqlCondition, Object value) {
		super();
		this.sqlCondition = sqlCondition;
		this.value = value;
	}

	public SQLCondition getSqlCondition() {
		return sqlCondition;
	}

	public Object getValue() {
		return value;
	}

}
