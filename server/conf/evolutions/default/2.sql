# --- First database schema

# --- !Ups

set ignorecase true;

create table expense_report (
  id                        bigint not null,
  user_id                   bigint not null,
  from_date                 datetime not null,
  to_date                   datetime not null,
  constraint pk_expense_report primary key (id))
;

create sequence expense_report_seq start with 1000;

create table expense_line (
  id                        bigint not null,
  expense_report_id         bigint not null,
  value_date                datetime not null,
  evidence_number           bigint not null,
  account                   varchar(50) not null,
  amount                    decimal not null,
  qualifier                 varchar(255) not null,
  description               varchar(255),
  constraint pk_expense_line primary key (id))
;

create sequence expense_line_seq start with 1000;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists expense_report;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists expense_report_seq;
