package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import org.bukkit.entity.Player;

class Holder {
    private final SkyBlock plugin;

    private final Driver driver;
    private final Player player;

    private final Board board;

    Holder(SkyBlock plugin, Driver driver, Player player) {
        this.plugin = plugin;
        this.driver = driver;
        this.player = player;

        this.board = new Board(plugin, player, driver.getRows().size());
        update();
    }

    void update() {
        PlaceholderManager placeholderManager = this.plugin.getPlaceholderManager();
        ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();

        if (scoreboardManager != null) {
            if (!scoreboardManager.isPlayerDisabled(this.player)) {
                this.board.setTitle(this.driver.getTitle().getLine());

                int count = 0;
                for (Row row : this.driver.getRows()) {
                    String line = placeholderManager.parsePlaceholders(this.player, row.getLine());
                    this.board.setLine(count, line);
                    count++;
                }

                this.player.setScoreboard(this.board.getBoard());
            } else {
                this.player.setScoreboard(scoreboardManager.getEmptyScoreboard());
            }
        }
    }

    Player getPlayer() {
        return this.player;
    }
}
