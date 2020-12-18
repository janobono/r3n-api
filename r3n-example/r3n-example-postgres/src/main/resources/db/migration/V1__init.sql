-- SEQUENCE
create sequence sq_hotel start with 1 increment by 1 no cycle;

-- TABLE
create table hotel
(
    id   bigint       not null,
    name varchar(255) not null,
    note text
);

-- PK
alter table hotel
    add constraint pk_hotel primary key (id);
