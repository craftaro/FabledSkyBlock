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
        
        board = new Board(plugin, player, driver.getRows().size());
        update();
    }

    void update() {
        PlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();

        if(scoreboardManager != null) {
            if (!scoreboardManager.isPlayerDisabled(player)) {
                board.setTitle(driver.getTitle().getLine());
        
                int count = 0;
                for(Row row : driver.getRows()) {
                    String line = placeholderManager.parsePlaceholders(player, row.getLine());
                    board.setLine(count, line);
                    count++;
                }
        
                this.player.setScoreboard(board.getBoard());
            } else {
                this.player.setScoreboard(scoreboardManager.getEmptyScoreboard());
            }
        }
    }
    
    Player getPlayer() {
        return player;
    }
    
}
