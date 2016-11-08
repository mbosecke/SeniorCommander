INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description)
select ID, NULL, 0, '!auction', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'User', NULL, 'General information about the ongoing auction, if any.'
FROM community;