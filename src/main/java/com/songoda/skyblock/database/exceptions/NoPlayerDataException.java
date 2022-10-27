package com.songoda.skyblock.database.exceptions;

public class NoPlayerDataException extends Exception {

    public NoPlayerDataException() {
        super("Player data does not exist in the database");
    }
}
