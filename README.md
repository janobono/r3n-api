# r3n-api

simple way to write SQL query in java

## Supported database servers

* postgreSQL
* oracle

## Maven plugin

Add plugin to your pom.xml

```xml
  <plugin>
    <groupId>sk.r3n</groupId>
    <artifactId>r3n-maven-plugin</artifactId>
    <version>${r3n.version}</version>
    <configuration>
      <targetPackage>sk.r3n.example.postgres</targetPackage>
      <jdbcDriver>org.postgresql.Driver</jdbcDriver>
      <jdbcUrl>jdbc:postgresql://{host}:{port}/{db}</jdbcUrl>
      <jdbcUser>{login}</jdbcUser>
      <jdbcPassword>{password}</jdbcPassword>
    </configuration>
  </plugin>
```
Execute plugin

```
  mvn r3n:r3n-gen
```
Result will be generated into your src dir to package *sk.r3n.example.postgres* and *sk.r3n.example.postgres.dto*

## r3n-sql
### sk.r3n.dto
Package with dto mapping annotations.
### sk.r3n.jdbc
Contains objects used to execute sql queries like PostgreSqlBuilder and OraSqlBuilder. SqlUtil contains few usefull methods.
### sk.r3n.sql
Contains objects to create queries, everything starts with Query object.

## Example
Test packages contains both, generated code and real examples how to use it. For example:

```
OraSqlBuilder sqlBuilder = new OraSqlBuilder();

Select select = Query.SELECT(T_BASE_TYPES.columns()).page(0, 5)
                     .FROM(Query.SELECT(T_BASE_TYPES.columns()).FROM(TABLE.T_BASE_TYPES()))
                     .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

List<Object[]> rows = sqlBuilder.select(connection, select);
```
Or select direct list of annotated objects

```
PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();
Dto dto = new Dto();

Select select = Query.SELECT(T_BASE_TYPES.columns()).page(0, 1).FROM(TABLE.T_BASE_TYPES()).ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

List<TBaseTypesSO> typesList = sqlBuilder.select(connection, select, TBaseTypesSO.class);
```

## Licence

Apache 2.0
