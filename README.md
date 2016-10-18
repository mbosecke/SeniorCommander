# SeniorCommander

A chat/moderation bot, primarly focused on twitch.

# Database Setup
* Have an available database (empty) and a user that can connect to it.
* Copy `/src/main/resources/reference.conf` to `/src/main/resources/application.conf` and populate with database settings.
* Run main method in `SeniorCommander` which will invoke [flyway](https://flywaydb.org/) to create all necessary database tables.

# Channel configuration
* Create a community:
```sql
insert into community(id, name) values (1, 'My Twitch Community');
```

* Configure a twitch channel:
```sql
insert into channel(community_id, type) values (1, 'irc');

insert into channel_setting(channel_id, key, value) values (1, 'server', 'irc.chat.twitch.tv');
insert into channel_setting(channel_id, key, value) values (1, 'port', '6667');
insert into channel_setting(channel_id, key, value) values (1, 'username', 'billy');
insert into channel_setting(channel_id, key, value) values (1, 'password', 'oauth:1234');
insert into channel_setting(channel_id, key, value) values (1, 'channel', '#billy');
```

* Configure a discord channel:
```sql
insert into channel(community_id, type) values (1, 'discord');

insert into channel_setting(channel_id, key, value) values (2, 'guild', 'billy');
insert into channel_setting(channel_id, key, value) values (2, 'channel', 'general');
insert into channel_setting(channel_id, key, value) values (2, 'token', '?');
```

* Add the commands:
```sql

-- quote crud
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!quote add', 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', true, 'MODERATOR', NULL, 'Add a new quote');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!quote delete', 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', true, 'MODERATOR', NULL, 'Delete an existing quote');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!quote edit', 'com.mitchellbosecke.seniorcommander.extension.core.command.QuoteCrud', true, 'MODERATOR', NULL, 'Edit an existing quote');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!quote', 'com.mitchellbosecke.seniorcommander.extension.core.command.RandomQuote', true, 'USER', NULL, 'Retrieve a random quote. Use "!quote author" to retrieve a random quote from a particular person.');

-- timer crud
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!timer add', 'com.mitchellbosecke.seniorcommander.extension.core.command.TimerCrud', true, 'MODERATOR', NULL, 'Add a new timer');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!timer delete', 'com.mitchellbosecke.seniorcommander.extension.core.command.TimerCrud', true, 'MODERATOR', NULL, 'Delete an existing timer');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!timer enable', 'com.mitchellbosecke.seniorcommander.extension.core.command.TimerCrud', true, 'MODERATOR', NULL, 'Enable an existing timer');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!timer disable', 'com.mitchellbosecke.seniorcommander.extension.core.command.TimerCrud', true, 'MODERATOR', NULL, 'Disable an existing timer');

--betting
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bet', 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', true, 'USER', NULL, 'Get information about the active bet');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bet open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', true, 'MODERATOR', NULL, 'Open a new bet');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bet cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', true, 'MODERATOR', NULL, 'Cancel the active bet');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bet close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', true, 'MODERATOR', NULL, 'Close the active bet, preventing new bets from being made');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bet winner', 'com.mitchellbosecke.seniorcommander.extension.core.command.Betting', true, 'MODERATOR', NULL, 'Declare the winner of the active bet');

-- points
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!points', 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', true, 'USER', NULL, 'Get the number of points that you or another user has');

-- command crud
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command add', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Add a new command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command edit', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Edit an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command delete', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Delete an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command enable', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Enable an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command disable', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Disable an existing command');

--giveaways
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Open a new giveaway');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Cancel the active giveaway');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Close the active giveaway, preventing new entries from being made');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway draw', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Declare the winner of the active giveaway');

-- misc
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!roll', 'com.mitchellbosecke.seniorcommander.extension.core.command.Roll', true, 'USER', NULL, 'Roll a die');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!roulette', 'com.mitchellbosecke.seniorcommander.extension.core.command.Roulette', true, 'USER', NULL, 'Take a risk');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!advice', 'com.mitchellbosecke.seniorcommander.extension.core.command.Advice', true, 'USER', NULL, 'Get some life advice');
```

* Add timers
```sql

INSERT INTO timer (community_sequence, message, implementation, interval, chat_lines, enabled, channel_id, description) VALUES (1, NULL, 'com.mitchellbosecke.seniorcommander.extension.core.timer.ModAudit', 600, NULL, true, 1, 'Tracks mods of a twitch channel');
INSERT INTO timer (community_sequence, message, implementation, interval, chat_lines, enabled, channel_id, description) VALUES (2, NULL, 'com.mitchellbosecke.seniorcommander.extension.core.timer.TwitchOnlineChecker', 60, NULL, true,  1, 'Checks if a twitch channel is online');
INSERT INTO timer (community_sequence, message, implementation, interval, chat_lines, enabled, channel_id, description) VALUES (3, NULL, 'com.mitchellbosecke.seniorcommander.extension.core.timer.PointTimer', 60, NULL, true,1, 'Distributes points to online users');
INSERT INTO timer (community_sequence, message, implementation, interval, chat_lines, enabled, channel_id, description) VALUES (4, NULL, 'com.mitchellbosecke.seniorcommander.extension.core.timer.FollowerAudit', 3600, NULL, true, 1, 'Tracks followers of a twitch channel');
```
* Restart bot. He will join your channel. Add him as a mod.
* Flag users as "bots" which means they don't show up in the leaderboards:
```sql
update community_user set bot=true where name = 'seniorcommander';
```


