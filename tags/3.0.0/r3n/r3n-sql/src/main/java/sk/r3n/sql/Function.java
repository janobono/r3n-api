package sk.r3n.sql;

import java.text.MessageFormat;

public class Function extends Column {

    private String function;

    public Function(String function, Column column) {
        super(column.getName(), column.getTable(), column.getDataType());
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public String nameWithAlias() {
        return MessageFormat.format(function, super.nameWithAlias());
    }

}
