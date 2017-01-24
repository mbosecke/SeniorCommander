--set search_path = bot, public;

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

insert into community_user (community_id, name, points, access_level, first_seen, last_chatted, bot)
values (1, 'seniorcommander', 0, 'ADMIN', current_date, current_date, true);


/**
 * Commands
 */

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
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!points give', 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', true, 'MODERATOR', NULL, 'Give points to a user');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!points take', 'com.mitchellbosecke.seniorcommander.extension.core.command.Points', true, 'MODERATOR', NULL, 'Take points from a user');

-- command crud
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command add', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Add a new command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command edit', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Edit an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command delete', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Delete an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command enable', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Enable an existing command');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!command disable', 'com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrud', true, 'MODERATOR', NULL, 'Disable an existing command');

-- giveaway
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Open a new giveaway');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Cancel the active giveaway');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Close the active giveaway, preventing new entries from being made');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!giveaway draw', 'com.mitchellbosecke.seniorcommander.extension.core.command.Giveaway', true, 'MODERATOR', NULL, 'Declare the winner of the active giveaway');

-- auction
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!auction open', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Open a new auction');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!auction cancel', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Cancel the active auction');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!auction close', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'MODERATOR', NULL, 'Close the active auction, declaring a winner');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!bid', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'USER', NULL, 'Make a bid');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!auction', 'com.mitchellbosecke.seniorcommander.extension.core.command.Auction', true, 'USER', NULL, 'General auction information');

-- misc
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!roll', 'com.mitchellbosecke.seniorcommander.extension.core.command.Roll', true, 'USER', NULL, 'Roll a die');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!roulette', 'com.mitchellbosecke.seniorcommander.extension.core.command.Roulette', true, 'USER', NULL, 'Take a risk');
INSERT INTO command (community_id, message, cooldown, trigger, implementation, enabled, access_level, alias, description) VALUES (1, NULL, 0, '!advice', 'com.mitchellbosecke.seniorcommander.extension.core.command.Advice', true, 'USER', NULL, 'Get some life advice');

