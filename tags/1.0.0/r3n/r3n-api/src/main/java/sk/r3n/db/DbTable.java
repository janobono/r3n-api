package sk.r3n.db;

import java.util.ArrayList;
import java.util.List;

public abstract class DbTable {

	protected String tableName;

	protected SQLColumn[] ids;

	protected SQLColumn[] values;

	protected SQLColumn[] state;

	protected SQLColumn[] columns;

	protected SQLColumn[] valuesAndStates;

	public DbTable() {
		super();
	}

	public SQLColumn[] getColumns() {
		if (columns == null) {
			List<SQLColumn> list = new ArrayList<SQLColumn>();
			for (int i = 0; i < ids.length; i++) {
				list.add(ids[i]);
			}
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			for (int i = 0; i < state.length; i++) {
				list.add(state[i]);
			}
			columns = new SQLColumn[list.size()];
			columns = list.toArray(columns);
		}
		return columns;
	}

	public SQLColumn[] getValuesAndStates() {
		if (valuesAndStates == null) {
			List<SQLColumn> list = new ArrayList<SQLColumn>();
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			for (int i = 0; i < state.length; i++) {
				list.add(state[i]);
			}
			valuesAndStates = new SQLColumn[list.size()];
			valuesAndStates = list.toArray(valuesAndStates);
		}
		return valuesAndStates;
	}

	public SQLColumn[] getIds() {
		return ids;
	}

	public SQLColumn[] getState() {
		return state;
	}

	public String getTableName() {
		return tableName;
	}

	public SQLColumn[] getValues() {
		return values;
	}

}
