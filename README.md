# SeniorCommander

A chat/moderation bot, primarly focused on twitch.

# Database Setup
* Have an available database (empty) and a user that can connect to it.
* Copy `/src/main/resources/reference.conf` to `/src/main/resources/application.conf` and populate with database settings.
* Run main method in `SeniorCommander` which will invoke [flyway](https://flywaydb.org/) to create all necessary database tables.

# Channel configuration
* Create a community:
````sql
insert into community(name) values ('My Twitch Community');
```
* Configure a twitch channel:
```sql
insert into channel(community_id, type) 
select id, 'irc'
from community;

insert into channel_setting(channel_id, key, value) values (?, 'server', 'irc.chat.twitch.tv');
insert into channel_setting(channel_id, key, value) values (?, 'port', '6667');
insert into channel_setting(channel_id, key, value) values (?, 'username', 'billy');
insert into channel_setting(channel_id, key, value) values (?, 'password', 'oauth:1234');
insert into channel_setting(channel_id, key, value) values (?, 'channel', '#billy');
```
* Restart bot


