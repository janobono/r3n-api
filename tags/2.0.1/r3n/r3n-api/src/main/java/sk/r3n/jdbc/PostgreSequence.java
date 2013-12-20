package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSequence implements Sequence {

    private String name;

    public PostgreSequence(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof PostgreSequence) {
            PostgreSequence sequenceObj = (PostgreSequence) obj;
            result = sequenceObj.name.equals(name);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String nextval() {
        StringBuilder sb = new StringBuilder();
        sb.append("nextval('").append(name).append("')");
        return sb.toString();
    }

    @Override
    public long nextVal(Connection connection) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(nextval());

        long result;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql.toString());
            resultSet.next();
            result = resultSet.getLong(1);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }
        return result;
    }

}
