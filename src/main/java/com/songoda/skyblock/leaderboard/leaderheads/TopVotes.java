package com.songoda.skyblock.leaderboard.leaderheads;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.Leaderboard.Type;
import com.songoda.skyblock.visit.Visit;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.Map.Entry;

public class TopVotes extends DataCollector {

    private final SkyBlock plugin;

    public TopVotes(SkyBlock plugin) {
        super("topvotes", plugin.getDescription().getName(), BoardType.DEFAULT, "&bTop Votes", "topvotes",
                Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}",
                        ChatColor.WHITE + "{amount} Votes", ChatColor.DARK_GRAY + "-=+=-"),
                true, UUID.class);

        this.plugin = plugin;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {
        
        List<Leaderboard> leaderboards = plugin.getLeaderboardManager().getLeaderboard(Type.Votes);
        Map<UUID, Double> topLevels = new HashMap<>(leaderboards.size());

        for (Leaderboard leaderboard : leaderboards) {
            Visit visit = leaderboard.getVisit();
            topLevels.put(visit.getOwnerUUID(), (double) visit.getVoters().size());
        }

        return LeaderHeadsAPI.sortMap(topLevels);
    }
}
