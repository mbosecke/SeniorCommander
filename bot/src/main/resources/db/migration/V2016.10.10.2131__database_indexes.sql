CREATE INDEX community_name_idx ON community (name ASC NULLS LAST);

CREATE INDEX community_user_name_idx ON community_user (name ASC NULLS LAST);