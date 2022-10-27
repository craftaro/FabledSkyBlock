package com.songoda.skyblock.database.exceptions;

public class NoIslandDataException extends Exception {

    public NoIslandDataException() {
        super("Island data does not exist in the database");
    }
}
