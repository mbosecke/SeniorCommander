create table community (
  id serial not null,
  name character varying(50),
  constraint community_pk primary key (id)
);

alter table community owner to seniorcommander;