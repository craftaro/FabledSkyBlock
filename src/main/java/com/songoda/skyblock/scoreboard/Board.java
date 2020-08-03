package com.songoda.skyblock.scoreboard;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

class Board {
    
    private final Player player;
    private final SkyBlock plugin;
    public Scoreboard board;
    private final Objective objective;

    private final HashMap<Integer, String> cache = new HashMap<>();

    public Board(SkyBlock plugin, Player player, int lineCount) {
        this.player = player;
        this.plugin = plugin;
        this.board = this.plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = this.board.registerNewObjective("sb1", "sb2");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("...");

        for(int i = 0; i < lineCount;i++) {
            Team t = this.board.registerNewTeam(i + "");
            t.addEntry(ChatColor.values()[i] + "");
            
            this.objective.getScore(ChatColor.values()[i] + "").setScore(lineCount - i);
        }
        
        //this.player.setScoreboard(this.board);
    }

    public void setTitle(String string) {
        PlaceholderManager placeholderManager = plugin.getPlaceholderManager();

        if(string == null) string = "";
        string = placeholderManager.parsePlaceholders(player, string);

        if (!cache.containsKey(-1) || !cache.get(-1).equals(string)) {
            cache.remove(-1);
            cache.put(-1, string);
            objective.setDisplayName(string);
        }
    }

    public void setLine(int line, String string) {
        Team t = board.getTeam(String.valueOf(line));
        if(string == null) string = "";

        if ((!cache.containsKey(line) || !cache.get(line).equals(string)) && t != null) {
            cache.remove(line);
            cache.put(line, string);

            ScoreboardLine parts;

            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)) {
                parts = convertIntoPieces(string, 16);
            } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)){
                parts = convertIntoPieces(string, 64);
            } else {
                parts = convertIntoPieces(string, 8);
            }

            t.setPrefix(parts.getPrefix());
            t.setSuffix(parts.getSuffix());
        }
    }
    
    private ScoreboardLine convertIntoPieces(String line, int allowed_line_size) {
        String prefixLine = line.substring(0, Math.min(line.length(), allowed_line_size));
        String suffixLine = "";
        if(line.length() > allowed_line_size) {
            suffixLine = line.substring(allowed_line_size);
        }

        if (prefixLine.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            prefixLine = ChatColor.translateAlternateColorCodes(
                    ChatColor.COLOR_CHAR, prefixLine.substring(0, prefixLine.length() - 1));
            suffixLine = ChatColor.translateAlternateColorCodes(
                    ChatColor.COLOR_CHAR, ChatColor.COLOR_CHAR + suffixLine);
        } else {
            String lastColorCodes;

            if (prefixLine.contains(String.valueOf(ChatColor.COLOR_CHAR))) {
                String[] colorCodes = prefixLine.split(String.valueOf(ChatColor.COLOR_CHAR));
                String lastColorCodeText = colorCodes[colorCodes.length - 1];
                lastColorCodes = ChatColor.COLOR_CHAR +
                        lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1));

                if ((colorCodes.length >= 2) &&
                        (lastColorCodes.equals(String.valueOf(ChatColor.BOLD)) ||
                                lastColorCodes.equals(String.valueOf(ChatColor.STRIKETHROUGH)) ||
                                lastColorCodes.equals(String.valueOf(ChatColor.UNDERLINE)) ||
                                lastColorCodes.equals(String.valueOf(ChatColor.ITALIC)) ||
                                lastColorCodes.equals(String.valueOf(ChatColor.MAGIC)))) {
                    lastColorCodeText = colorCodes[colorCodes.length - 2];
                    lastColorCodes = ChatColor.COLOR_CHAR +
                            lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1)) + lastColorCodes;
                }
            } else {
                lastColorCodes = ChatColor.WHITE.toString();
            }

            prefixLine = ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, prefixLine);
            suffixLine = ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, lastColorCodes + suffixLine);
        }
        return new ScoreboardLine(prefixLine, suffixLine, allowed_line_size);
    }
}
