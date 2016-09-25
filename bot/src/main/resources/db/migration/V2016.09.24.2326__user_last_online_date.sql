alter table community_user add last_online timestamp;
alter table community_user add time_online integer not null default 0;