package com.mitchellbosecke.seniorcommander.utils;

import com.typesafe.config.ConfigFactory;

/**
 * Created by mitch_000 on 2017-02-09.
 */
public class ConfigUtils {

    public static String getString(String key){
        return ConfigFactory.load().getConfig("seniorcommander").getString(key);
    }

    public static int getInt(String key){
        return ConfigFactory.load().getConfig("seniorcommander").getInt(key);
    }
}
