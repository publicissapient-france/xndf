# --- First database schema

# --- !Ups

set ignorecase true;

create table users (
  id                        bigint not null,
  name                      varchar(255),
  email	                      varchar(255) not null,
  verifiedId				varchar(255) not null,
  constraint pk_user primary key (id))
;

create sequence users_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists users_seq;

