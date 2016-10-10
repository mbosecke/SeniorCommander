create table community_setting (
  id serial not null primary key,
  community_id integer references community(id) not null,
  key character varying(50) not null,
  value character varying(50)
);

ALTER TABLE community_setting
  ADD CONSTRAINT community_setting_key_uq UNIQUE (community_id, key);


alter table community_setting owner to seniorcommander;