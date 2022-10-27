package com.songoda.skyblock.database.migration;

import com.songoda.core.database.DataMigration;
import com.songoda.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {

    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {

        // Create islands table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + " `islands` (" +
                    "`uuid` INT(11) NOT NULL," +
                    "`owner` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "`originalOwner` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "`structure` VARCHAR(100) NOT NULL DEFAULT 'Default' COLLATE 'utf8mb4_general_ci'," +
                    "`size` INT(11) NOT NULL DEFAULT '51'," +
                    "`biome` VARCHAR(50) NOT NULL DEFAULT 'PLAINS' COLLATE 'utf8mb4_general_ci'," +
                    "`status` VARCHAR(11) NOT NULL DEFAULT 'WHITELISTED' COLLATE 'utf8mb4_general_ci'," +
                    "`border` BIT(1) NOT NULL DEFAULT b'1'," +
                    "`borderColor` VARCHAR(15) NOT NULL DEFAULT 'Blue' COLLATE 'utf8mb4_general_ci'," +
                    "`weatherSync` BIT(1) NOT NULL DEFAULT b'1'," +
                    "`weatherTime` INT(11) NOT NULL DEFAULT '6000'," +
                    "`weatherType` VARCHAR(15) NOT NULL DEFAULT 'CLEAR' COLLATE 'utf8mb4_general_ci'," +
                    "`maxMembers` INT(11) NOT NULL DEFAULT '3'," +
                    "`transactionHistory` BLOB NULL DEFAULT NULL," +
                    "PRIMARY KEY (`uuid`) USING BTREE" +
                    ")" +
                    "COLLATE='utf8mb4_general_ci'" +
                    "ENGINE=InnoDB" +
                    ";");

            //players table
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + " `players` (" +
                    "`uuid` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "`island` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "`textureSignature` MEDIUMTEXT NOT NULL DEFAULT 'K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=' COLLATE 'utf8mb4_general_ci'," +
                    "`textureValue` MEDIUMTEXT NOT NULL DEFAULT 'eyJ0aW1lc3RhbXAiOjE1MjkyNTg0MTE4NDksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19' COLLATE 'utf8mb4_general_ci'," +
                    "`playTime` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                    "`joinTime` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()," +
                    "`chatSpy` BIT(1) NOT NULL DEFAULT b'0'," +
                    "`spiedIslands` BLOB NULL DEFAULT NULL," +
                    "PRIMARY KEY (`uuid`) USING BTREE" +
                    ")" +
                    "COLLATE='utf8mb4_general_ci'" +
                    "ENGINE=InnoDB" +
                    ";");

            //Player cache table
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + " `players_cache` (" +
                    "`uuid` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "`name` VARCHAR(16) NOT NULL COLLATE 'utf8mb4_general_ci'," +
                    "PRIMARY KEY (`uuid`) USING BTREE" +
                    ")" +
                    "COLLATE='utf8mb4_general_ci'" +
                    "ENGINE=InnoDB" +
                    ";");
        }
    }

}