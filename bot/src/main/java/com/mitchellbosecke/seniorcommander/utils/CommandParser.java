package com.mitchellbosecke.seniorcommander.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-16.
 */
public class CommandParser {

    private int index = 0;

    private Pattern optionPattern = Pattern.compile("(\\S+)=(\\S+)");

    public ParsedCommand parse(String content){

        ParsedCommand result = new ParsedCommand();

        String[] words = content.split("\\s+");

        if(words.length >= 1) {

            // tokenizer
            result.setTrigger(words[0]);

            for(index = 1; index < words.length; index++){
                String word = words[index];

                if (word.startsWith("\"")) {

                    // parse the quoted text
                    result.setQuotedText(parseQuotedText(words));

                } else {
                    parseWord(result, word);
                }
            }
        }

        return result;
    }

    private String parseQuotedText(String[] words){
        StringBuilder builder = new StringBuilder();
        for(; index < words.length; index++){
            String word = words[index];
            builder.append(word);
            if(word.endsWith("\"")){
                break;
            }else{
                builder.append(" ");
            }
        }
        String full = builder.toString();

        // strip the two quotation marks
        return full.substring(1, full.length() - 1);
    }


    private void parseWord(ParsedCommand result, String word){
        Matcher matcher = optionPattern.matcher(word);
        if(matcher.matches()){
            result.getOptions().put(matcher.group(1), matcher.group(2));
        }else{
            result.getComponents().add(word);
        }
    }
}
