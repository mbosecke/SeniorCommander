CREATE TABLE auction (
  id serial NOT NULL PRIMARY KEY,
  community_id integer NOT NULL REFERENCES community(id),
  prize character varying(100) NOT NULL,
  closed timestamp without time zone,
  winning_community_user_id integer REFERENCES community_user(id),
  winning_bid integer
);

ALTER TABLE auction OWNER to seniorcommander;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
select ID, NULL, 0, '!auction open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Open a new auction'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
select ID, NULL, 0, '!auction cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Cancel the active auction'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!auction close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Close the active auction, declaring a winner.'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!bid', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'USER', NULL, 'Place a bid for the current auction'
FROM community;