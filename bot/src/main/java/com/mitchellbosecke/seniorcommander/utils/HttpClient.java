package com.mitchellbosecke.seniorcommander.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-24.
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private String baseUrl;
    private List<String> pathSegments = new ArrayList<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        header("User-Agent", "Java-Async-Http");
    }

    public HttpClient pathSegment(String pathSegment) {
        this.pathSegments.add(pathSegment);
        return this;
    }

    public HttpClient header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpClient param(String key, String value){
        this.params.put(key, value);
        return this;
    }

    public String get() {
        return perform("GET");
    }

    private String perform(String method) {
        HttpURLConnection connection = null;
        try {
            String urlString = constructUrl();
            URL url = new URL(urlString);
            logger.debug("Performing GET for URL [" + urlString + "]");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            connection.setUseCaches(false);
            connection.setDoOutput(false);

            int status = connection.getResponseCode();
            if (status == 200) {
                return readInputStream(connection.getInputStream());
            } else {
                logger.debug("HttpClient got an error status of " + status);
                return readInputStream(connection.getErrorStream());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readInputStream(InputStream inputStream) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            reader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return response.toString();
    }

    private String constructUrl() {
        StringBuilder builder = new StringBuilder(baseUrl);

        for (String pathSegment : pathSegments) {
            if (builder.charAt(builder.length() - 1) != '/' && pathSegment.charAt(0) != '/') {
                builder.append('/');
            }
            builder.append(pathSegment);
        }

        boolean firstParam = true;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (firstParam) {
                builder.append("?");
                firstParam = false;
            } else {
                builder.append("&");
            }
            builder.append(param.getKey()).append("=").append(param.getValue());
        }
        return builder.toString();
    }
}
