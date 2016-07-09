package com.mitchellbosecke.seniorcommander.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class MessageUtils {

    private static Pattern targetedMessage = Pattern.compile("@(\\w+),?\\s+?(.*)");

    public static String[] splitRecipient(String message){
        String recipient = null;
        Matcher matcher = targetedMessage.matcher(message);
        if (matcher.matches()) {
            recipient = matcher.group(1);
            message = matcher.group(2);
        }
        return new String[]{recipient, message};
    }
}
