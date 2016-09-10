create table chat_log (
  id serial not null primary key,
  channel_id integer not null references channel(id),
  message character varying (500) not null,
  community_user_id integer not null references community_user(id),
  date timestamp without time zone not null
);

alter table chat_log owner to seniorcommander;