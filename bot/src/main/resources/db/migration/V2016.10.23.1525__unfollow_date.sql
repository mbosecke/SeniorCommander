ALTER TABLE community_user ADD unfollowed timestamp without time zone;

UPDATE timer SET interval = 60 where implementation = 'com.mitchellbosecke.seniorcommander.extension.core.timer.FollowerAudit';