package com.mitchellbosecke.seniorcommander.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mitch_000 on 2017-01-24.
 */
public class NetworkUtils {

    public static String getLocalHostname() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostname;
    }
}
