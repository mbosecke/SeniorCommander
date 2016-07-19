set search_path = core, public;

insert into community (id, name) values(1, 'Playing Board Games');

insert into channel (community_id, type) values (1, 'socket');

insert into channel_setting (channel_id, key, value)
select id, 'port', '4444'
from channel where type = 'socket';

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!roll', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Roll', 'USER');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!advice', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.Advice', 'USER');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!command', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', 'MODERATOR');

insert into command (community_id, trigger, cooldown, implementation, access_level)
values (1, '!quote', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.RandomQuote', 'USER');

insert into command (community_id, cooldown, trigger, implementation, access_level)
values (1, '!quote delete', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR')

insert into command (community_id, cooldown, trigger, implementation, access_level)
values (1, '!quote add', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR')

insert into command (community_id, cooldown, trigger, implementation, access_level)
values (1, '!quote edit', 0, 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', 'MODERATOR')