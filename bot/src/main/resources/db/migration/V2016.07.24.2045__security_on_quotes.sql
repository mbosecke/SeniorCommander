alter table quote add community_sequence integer not null;

alter table quote add constraint quote_community_sequence unique (community_id, community_sequence);