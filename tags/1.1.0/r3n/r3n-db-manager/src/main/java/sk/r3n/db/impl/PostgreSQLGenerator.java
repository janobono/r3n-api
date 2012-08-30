package sk.r3n.db.impl;

public class PostgreSQLGenerator extends DefaultSQLGenerator {

    public PostgreSQLGenerator() {
        super();
        LIKE_SQL = " ilike ";
    }

    @Override
    public String putIntoQuotes(String string) {
        return super.putIntoQuotes(string).toLowerCase();
    }
}