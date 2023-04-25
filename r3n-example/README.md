# r3n-example

- [springboot + liquibase + r3n + oracle](./r3n-example-ora/README.md)
- [springboot + flyway + r3n + postgreSQL](./r3n-example-postgres/README.md)

[Flyway](https://flywaydb.org/) doesn't support oracle in community edition so for oracle example
[Liquibase](https://www.liquibase.org/) was used.

## environment variables

| Name                     | Default value | Description    |
|--------------------------|---------------|----------------|
| EXAMPLE_PORT             | 8080          | server port    |
| EXAMPLE_CONTEXT_PATH     | /api          | context path   |
| EXAMPLE_LOG_LEVEL        | debug         | log level      | 
| EXAMPLE_DB_URL           | *1, *2        | SQL URL        | 
| EXAMPLE_DB_USER          | app           | SQL user       | 
| EXAMPLE_DB_PASS          | app           | SQL password   |

- *1 postgreSQL value `jdbc:postgresql://localhost:5432/app`
- *2 oracle value `jdbc:oracle:thin:@localhost:1521:XE`
