alter table community_user add column first_seen timestamp;
alter table community_user add column last_chatted timestamp;

alter table community_user add constraint community_user_channel_name_uq unique (community_id, name);