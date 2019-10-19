package com.songoda.skyblock.leaderboard.leaderheads;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.visit.Visit;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.Map.Entry;

public class TopLevel extends DataCollector {

    private final SkyBlock skyblock;

    public TopLevel(SkyBlock skyblock) {
        super("toplevels", skyblock.getDescription().getName(), BoardType.DEFAULT, "&bTop Level", "toplevel",
                Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}",
                        ChatColor.WHITE + "{amount} Level", ChatColor.DARK_GRAY + "-=+=-"),
                true, UUID.class);

        this.skyblock = skyblock;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {

        List<Leaderboard> leaderboards = skyblock.getLeaderboardManager().getLeaderboard(Leaderboard.Type.Level);
        Map<UUID, Double> topLevels = new HashMap<>(leaderboards.size());

        for (int i = 0; i < leaderboards.size(); i++) {
            Leaderboard leaderboard = leaderboards.get(i);
            Visit visit = leaderboard.getVisit();
            topLevels.put(visit.getOwnerUUID(), (double) visit.getLevel().getLevel());
        }

        return LeaderHeadsAPI.sortMap(topLevels);
    }
}
