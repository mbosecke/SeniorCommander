create table online_channel_user (
  id serial not null primary key,
  channel_id integer references channel(id),
  community_user_id integer references community_user(id)
);


alter table online_channel_user add constraint online_channel_user_uq unique (channel_id, community_user_id);