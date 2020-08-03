package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import org.bukkit.entity.Player;

class Holder {

    private final SkyBlock plugin;

    private final Driver driver;
    public final Player player;
    
    private final Board board;

    public Holder(SkyBlock plugin, Driver driver, Player player) {
        this.plugin = plugin;
        this.driver = driver;
        this.player = player;
        
        board = new Board(plugin, player, driver.getRows().size());
        update();
    }

    public void update() {
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

                this.player.setScoreboard(board.board);
            } else {
                this.player.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
            }
        }
    }
    
}
