create table quote (
  id serial not null primary key,
  community_id integer not null references community(id),
  author character varying (100),
  content character varying(500)
);

alter table quote owner to seniorcommander;