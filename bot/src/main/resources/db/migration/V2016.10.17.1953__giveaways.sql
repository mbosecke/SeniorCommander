CREATE TABLE giveaway (
  id serial NOT NULL PRIMARY KEY,
  community_id integer NOT NULL REFERENCES community(id),
  keyword character varying(100),
  closed timestamp without time zone
);

ALTER TABLE giveaway OWNER to seniorcommander;

CREATE TABLE giveaway_entry (
  id serial NOT NULL PRIMARY KEY,
  community_user_id integer NOT NULL REFERENCES community_user(id),
  giveaway_id integer NOT NULL REFERENCES giveaway(id),
  winner boolean NOT NULL
);

ALTER TABLE giveaway_entry OWNER to seniorcommander;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
select ID, NULL, 0, '!giveaway open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Open a new giveaway'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
select ID, NULL, 0, '!giveaway cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Cancel the active giveaway'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!giveaway close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Close the active giveaway, preventing new entries from being made'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!giveaway draw', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Declare the winner of the active giveaway'
FROM community;