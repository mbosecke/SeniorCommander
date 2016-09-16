package com.mitchellbosecke.seniorcommander.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-07-16.
 */
public class ParsedCommand {

    private String trigger;

    private List<String> components = new ArrayList<>();

    private String quotedText;

    private Map<String, String> options = new HashMap<>();

    private String originalCommand;

    public String getTrigger() {
        return trigger;
    }

    public List<String> getComponents() {
        return components;
    }

    public String getQuotedText() {
        return quotedText;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public void setQuotedText(String quotedText) {
        this.quotedText = quotedText;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getOriginalCommand() {
        return originalCommand;
    }

    public void setOriginalCommand(String originalCommand) {
        this.originalCommand = originalCommand;
    }

    public String getOption(String... keys) {
        for (String key : keys) {
            if (options.containsKey(key)) {
                return options.get(key);
            }
        }
        return null;
    }
}
