create table command (
  id serial not null primary key,
  community_id integer not null references community(id),
  trigger character varying (50),
  message character varying(200),
  cooldown integer
);

create table command_log (
  id serial not null primary key,
  command_id integer not null references command(id),
  community_user_id integer not null references community_user(id),
  log_date timestamp
);