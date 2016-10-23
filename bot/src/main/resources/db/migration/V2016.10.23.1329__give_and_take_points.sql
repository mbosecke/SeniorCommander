INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!points give', 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', true, 'MODERATOR', NULL, 'Give points to a user'
FROM community;

INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
SELECT id, NULL, 0, '!points take', 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', true, 'MODERATOR', NULL, 'Take points from a user'
FROM community;