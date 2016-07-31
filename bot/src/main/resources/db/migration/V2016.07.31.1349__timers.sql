create table timer (
  id serial not null primary key,
  community_id integer not null references community(id),
  community_sequence integer not null,
  message character varying (500),
  implementation character varying (200),
  interval integer not null,
  chat_lines integer,
  enabled boolean not null
);

alter table timer owner to seniorcommander;