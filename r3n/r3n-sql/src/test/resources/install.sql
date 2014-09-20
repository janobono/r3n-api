--SEQUENCES
CREATE SEQUENCE H_SEQUENCE START 1;

--TABLES
CREATE TABLE PERSON
(
  ID                IDENTITY NOT NULL,
  CREATED           TIMESTAMP NOT NULL,
  CREATOR           VARCHAR(255) NOT NULL,
  TYPE              SMALLINT NOT NULL,
  PERSONAL_ID       VARCHAR(10) NOT NULL,
  FIRST_NAME        VARCHAR(255) NOT NULL,
  FIRST_NAME_SCDF   VARCHAR(255) NOT NULL,
  LAST_NAME         VARCHAR(255) NOT NULL,
  LAST_NAME_SCDF    VARCHAR(255) NOT NULL,
  BIRTH_DATE        DATE NOT NULL,
  NOTE              VARCHAR(1024)
);

CREATE TABLE ADDRESS
(
  ID                IDENTITY NOT NULL,
  PERSON_FK         BIGINT NOT NULL,
  TYPE              SMALLINT NOT NULL,
  STREET            VARCHAR(255) NOT NULL,
  CITY              VARCHAR(255) NOT NULL,
  STATE             VARCHAR(255) NOT NULL,
  POST_CODE         VARCHAR(6) NOT NULL,
  TEST_TIME         TIME,
  TEST_BLOB         BLOB,
  VALUE             DECIMAL NOT NULL
);

--FK
ALTER TABLE ADDRESS ADD CONSTRAINT FK_PA
    FOREIGN KEY (PERSON_FK) REFERENCES PERSON (ID) ON DELETE CASCADE ON UPDATE CASCADE;

--INDEX
CREATE UNIQUE INDEX UQ_PERSON ON PERSON (PERSONAL_ID ASC);
