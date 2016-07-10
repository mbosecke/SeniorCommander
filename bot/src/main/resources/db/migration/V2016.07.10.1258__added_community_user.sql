create table community_user (
  id serial not null primary key,
  community_id integer references community(id),
  name character varying(50),
  points integer default 0,
  access_level character varying(15)
);

alter table community_user owner to seniorcommander;