set search_path = core, public;

/**
 * Create community and channel
 */
insert into community (id, name) values(1, 'Playing Board Games');

insert into channel (community_id, type) values (1, 'socket');

insert into channel_setting (channel_id, key, value)
select id, 'port', '4444'
from channel where type = 'socket';

/**
 * Users
 */
insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'user', 0, 'USER', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'follower', 0, 'FOLLOWER', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'subscriber', 0, 'SUBSCRIBER', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'regular', 0, 'REGULAR', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'moderator', 0, 'MODERATOR', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'owner', 0, 'OWNER', current_date, current_date);

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted)
values (1, 'admin', 0, 'ADMIN', current_date, current_date);


/**
 * Commands
 */
insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!roll', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Roll', 'USER');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!advice', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Advice', 'USER');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!command', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!quote', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.RandomQuote', 'USER');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!quote delete', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!quote add', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!quote edit', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!timer', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.TimerCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!bet', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!points', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', 'USER');

