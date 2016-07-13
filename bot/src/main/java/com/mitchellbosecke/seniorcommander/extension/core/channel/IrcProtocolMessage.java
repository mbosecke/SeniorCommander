package com.mitchellbosecke.seniorcommander.extension.core.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by mitch_000 on 2016-07-07.
 */
public class IrcProtocolMessage {

    private String nick;

    private String login;

    private String hostname;

    private String command;

    private List<String> params;

    public IrcProtocolMessage(String line) {

        StringTokenizer tokenizer = new StringTokenizer(line);
        String senderInfo = tokenizer.nextToken();
        command = tokenizer.nextToken().toUpperCase();

        int exclamation = senderInfo.indexOf("!");
        int at = senderInfo.indexOf("@");
        if (senderInfo.startsWith(":")) {
            if (exclamation > 0 && at > 0 && exclamation < at) {
                nick = senderInfo.substring(1, exclamation);
                login = senderInfo.substring(exclamation + 1, at);
                hostname = senderInfo.substring(at + 1);
            }
        }

        params = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String param = tokenizer.nextToken();
            if (param.startsWith(":")) {
                param = param.substring(1);
            }
            params.add(param);
        }
    }

    public String getCommand() {
        return command;
    }

    public String getHostname() {
        return hostname;
    }

    public String getLogin() {
        return login;
    }

    public String getNick() {
        return nick;
    }

    public List<String> getParams() {
        return params;
    }

    public String getLastParam(){
        return params.get(params.size() - 1);
    }
}
