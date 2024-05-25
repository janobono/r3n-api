# postgreSQL

- [dto package](./src/main/java/sk/r3n/example/dal/domain/r3n/dto)
- [meta package](./src/main/java/sk/r3n/example/dal/domain/r3n/meta)
- [domain model](./src/main/resources/db/migration/V1__init.sql)
- [integration test](./src/test/java/sk/r3n/example/api/controller/HotelControllerIT.java)

## local database for development

- local database for development [docker compose](./docker-compose.yml)

### start db

```
docker compose up
```

### stop db

```
docker compose down
```
