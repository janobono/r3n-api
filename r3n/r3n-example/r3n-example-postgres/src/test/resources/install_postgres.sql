--SEQUENCES
create sequence test_sequence start 1 cache 50;

--TABLES
create table t_base_types(
id                bigint not null,
t_short           smallint not null,
t_integer         integer not null,
t_long            bigint not null,
t_big_decimal     numeric not null,
t_string_char     char(10) not null,
t_string_text     text not null,
t_string_varchar  varchar(255) not null,
t_string_scdf     varchar(255) not null,
t_blob            bytea not null,
t_time_stamp      timestamp not null,
t_time            time not null,
t_date            date not null,
t_boolean         boolean not null
);

create table t_join
(
  id                bigint not null,
  t_base_types_fk   bigint not null,
  t_join_string     varchar(255) not null
);

-- PK
alter table t_base_types add primary key (id);
alter table t_join add primary key (id);

--FK
alter table t_join add constraint fk_bt 
    foreign key (t_base_types_fk) references t_base_types (id) on delete cascade on update cascade;
