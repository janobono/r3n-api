# r3n-api

Simple way to write SQL query in java.

If you can't or don't want to use:

- [JPA](https://github.com/eclipse-ee4j/jpa-api)
- [JOOQ](https://www.jooq.org/)
- [query dsl](http://www.querydsl.com/)

is `r3n-sql` ready for you ;o)

## foreword

It was sometime in the summer of 2012 when I met a somebody who wanted to create a program his nickname was r3ner. I
didn't know where to start but because I am Java Developer, I programmed it all in Java. Set of libraries used with this
program was designed. Originally also contained Swing components
([tag 1.0.0](https://github.com/janobono/r3n-api/tree/1.0.0/r3n/r3n-ui)), over time, it transformed into the form in
which it is currently. Some things are historical, but I don't want to delete them. Use freely, copy redistribute or
whatever you need license is just formal.

## project structure

### r3n-util

Just bunch of simple utils. [Some examples here...](./r3n-util/README.md)

### r3n-sql

Final library to write SQL queries in java. [More here...](./r3n-sql/README.md)

### r3n-maven-plugin

Maven plugin to generate meta object useful in query creation process. [Look here...](./r3n-maven-plugin/README.md)

### r3n-example

[Example project](./r3n-example/README.md)

## build

- [OpenJDK 8](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/download.cgi)

with tests:
```
mvn clean install
```

Oracle DB image is massive so maybe without tests:
```
mvn clean install -DskipTests
```
