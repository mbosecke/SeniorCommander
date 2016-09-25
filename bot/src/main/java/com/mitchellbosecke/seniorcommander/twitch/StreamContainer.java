package com.mitchellbosecke.seniorcommander.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mitch_000 on 2016-09-24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamContainer {

    private Stream stream;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
