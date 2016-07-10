create table channel (
  id serial not null primary key,
  community_id integer references community(id),
  type character varying(25)
);

alter table channel owner to seniorcommander;

create table channel_setting (
  id serial not null primary key,
  channel_id integer references channel(id),
  key character varying(25),
  value character varying(50)
);

alter table channel_setting owner to seniorcommander;