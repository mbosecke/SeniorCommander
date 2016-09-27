alter table timer add last_executed timestamp;

alter table timer drop column community_id;

alter table timer add channel_id integer references channel(id);