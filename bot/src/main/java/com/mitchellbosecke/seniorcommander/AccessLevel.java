package com.mitchellbosecke.seniorcommander;

/**
 * Created by mitch_000 on 2016-07-18.
 */
public enum AccessLevel {

    USER(1), FOLLOWER(2), SUBSCRIBER(3), REGULAR(4), MODERATOR(5), OWNER(6), ADMIN(7);

    private final int level;

    AccessLevel(int level) {
        this.level = level;
    }

    public boolean hasAccess(AccessLevel requiredAccessLevel) {
        return this.level >= requiredAccessLevel.level;
    }
}
