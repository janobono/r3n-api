package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OraSequence implements Sequence {

    private String name;

    public OraSequence(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof OraSequence) {
            OraSequence sequenceObj = (OraSequence) obj;
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
        sb.append(name);
        sb.append(".");
        sb.append("NEXTVAL");
        return sb.toString();
    }

    @Override
    public long nextVal(Connection connection) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(nextval()).append(" FROM DUAL");

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

    @Override
    public String toString() {
        return "Sequence{" + "name=" + name + '}';
    }

}
