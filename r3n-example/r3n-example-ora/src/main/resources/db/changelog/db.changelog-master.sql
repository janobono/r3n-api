--liquibase formatted sql

--changeset janobono:1
ALTER SESSION SET NLS_LENGTH_SEMANTICS=CHAR;
create sequence sq_hotel minvalue 1 increment by 1 start with 1 nocache noorder nocycle;
create table hotel
(
    id   number(19, 0) not null,
    name varchar2(255) not null,
    note clob
);
alter table hotel
    add constraint pk_hotel primary key (id);
--rollback drop sequence sq_hotel;
--rollback drop table hotel;
