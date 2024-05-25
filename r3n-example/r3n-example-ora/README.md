# oracle

- [dto package](./src/main/java/sk/r3n/example/dal/domain/r3n/dto)
- [meta package](./src/main/java/sk/r3n/example/dal/domain/r3n/meta)
- [domain model](./src/main/resources/db/changelog/db.changelog-master.sql)
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

### prepare app db

- user/password: **system**/**oracle**
- execute [script](./src/test/resources/create_user.sql) to create **app** user

## generate r3n objects

```
mvn r3n:r3n-gen
```
