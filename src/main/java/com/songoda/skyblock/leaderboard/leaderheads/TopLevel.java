package com.songoda.skyblock.leaderboard.leaderheads;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.visit.Visit;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TopLevel extends DataCollector {
    private final SkyBlock plugin;

    public TopLevel(SkyBlock plugin) {
        super("toplevels", plugin.getDescription().getName(), BoardType.DEFAULT, "&bTop Level", "toplevel",
                Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}", ChatColor.WHITE + "{amount} Level", ChatColor.DARK_GRAY + "-=+=-"), true, UUID.class);

        this.plugin = plugin;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {
        List<Leaderboard> leaderboards = this.plugin.getLeaderboardManager().getLeaderboard(Leaderboard.Type.LEVEL);
        Map<UUID, Double> topLevels = new HashMap<>(leaderboards.size());

        for (Leaderboard leaderboard : leaderboards) {
            Visit visit = leaderboard.getVisit();
            topLevels.put(visit.getOwnerUUID(), (double) visit.getLevel().getLevel());
        }

        return LeaderHeadsAPI.sortMap(topLevels);
    }
}
