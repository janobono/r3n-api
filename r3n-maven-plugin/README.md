# r3n-maven-plugin

## pom

```xml

<plugin>
    <groupId>sk.r3n</groupId>
    <artifactId>r3n-maven-plugin</artifactId>
    <version>5.0.0</version>
    <configuration>
        <overwrite>OVERWRITE</overwrite>
        <targetPackage>TARGET_PACKAGE</targetPackage>
        <jdbcDriver>JDBC_DRIVER</jdbcDriver>
        <jdbcUrl>JDBC_URL</jdbcUrl>
        <jdbcUser>JDBC_USER</jdbcUser>
        <jdbcPassword>JDBC_PASSWORD</jdbcPassword>
    </configuration>
    <dependencies>
        JDBC_DRIVER_DEPENDENCY
    </dependencies>
</plugin>
```

|PARAMETER|DEFINITION|EXAMPLE|
|---|---|---|
|OVERWRITE|Flag to rewrite existing files. Default value is **true**|true, false|
|TARGET_PACKAGE|Target package for generated java files.|your.project.dal.r3n|
|JDBC_DRIVER|Jdbc driver class.|PostgreSQL: org.postgresql.Driver, Oracle: oracle.jdbc.driver.OracleDriver|
|JDBC_URL|Jdbc url.|PostgreSQL: jdbc:postgresql://{host}:{port}/{db}, Oracle: jdbc:oracle:thin:@{host}:{port}:{db}|
|JDBC_USER|Database user name.||
|JDBC_PASSWORD|Database user password.||
|JDBC_DRIVER_DEPENDENCY|Driver jar dependency.|PostgreSQL: *1, Oracle: *2|

- *1

```xml

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${org.postgresql.version}</version>
</dependency>
```

- *2

```xml

<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>${com.oracle.version}</version>
</dependency>
```

## execute

```
mvn r3n:r3n-gen
```

### meta objects

Meta database objects will be saved in `r3n` package with prefix `Meta`.

- example MetaSequence:

```java
package sk.r3n.example.dal.domain.r3n;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    SQ_HOTEL("sq_hotel");

    private final String sequenceName;

    MetaSequence(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}

```

- example MetaTable:

```java
package sk.r3n.example.dal.domain.r3n;

import sk.r3n.sql.Table;

public enum MetaTable {

    HOTEL("hotel", "t1");

    private final String tableName;

    private final String tableAlias;

    MetaTable(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public Table table() {
        return new Table(tableName, tableAlias);
    }

    public Table table(String tableAlias) {
        return new Table(tableName, tableAlias);
    }
}

```

- example MetaColumn

```java
public enum MetaColumnHotel {

    ID("id", DataType.LONG),
    NAME("name", DataType.STRING),
    NOTE("note", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnHotel(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return new Column(columnName, MetaTable.HOTEL.table(), dataType);
    }

    public Column column(String tableAlias) {
        return new Column(columnName, MetaTable.HOTEL.table(tableAlias), dataType);
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
```

### dto objects

**D**atabase **T**ransfer **O**bjects will be stored in target package.

- example Dto:

```java
package sk.r3n.example.dal.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.dto.ColumnId;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class HotelDto implements Serializable {

    @ColumnId(table = "hotel", column = "id")
    private Long id;

    @ColumnId(table = "hotel", column = "name")
    private String name;

    @ColumnId(table = "hotel", column = "note")
    private String note;
}
```
