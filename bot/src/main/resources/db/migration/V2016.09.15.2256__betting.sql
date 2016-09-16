create table betting_game (
  id serial not null primary key,
  community_id integer not null references community(id)
);
alter table betting_game owner to seniorcommander;

create table betting_option (
  id serial not null primary key,
  betting_game_id integer not null references betting_game(id),
  value character varying(215)
);
alter table betting_option owner to seniorcommander;

