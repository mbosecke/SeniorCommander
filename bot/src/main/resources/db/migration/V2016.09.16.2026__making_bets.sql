create table bet (
  id serial not null primary key,
  community_user_id integer not null references community_user(id),
  betting_option_id integer not null references betting_option(id),
  amount integer not null
);
alter table bet owner to seniorcommander;

