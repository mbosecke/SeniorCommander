import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class IrcBot extends PircBot {

    private final MessageQueue messageQueue;

    public IrcBot(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }


    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        //super.onMessage(channel, sender, login, hostname, message);
        System.out.println(message);
        //messageQueue.addMessage(new Message(message));
    }

    public static void main(String[] args) throws IOException, IrcException {
        IrcBot bot = new IrcBot(new MessageQueue());

        Configuration config = new Configuration("config.properties");

        bot.setName(config.getProperty("username"));
        bot.connect(config.getProperty("irc.server"), Integer.valueOf(config.getProperty("irc.port")), config.getProperty("oauth.key"));
        bot.joinChannel(config.getProperty("irc.channel"));
    }
}
