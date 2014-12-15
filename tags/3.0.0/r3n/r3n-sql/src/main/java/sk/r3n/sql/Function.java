package sk.r3n.sql;

import java.text.MessageFormat;

public class Function extends Column {

    private final Object[] members;

    public Function(String function, DataType dataType, String... members) {
        super(function, null, dataType);
        this.members = members;
    }

    @Override
    public String nameWithAlias() {
        return MessageFormat.format(getName(), members);
    }

}
