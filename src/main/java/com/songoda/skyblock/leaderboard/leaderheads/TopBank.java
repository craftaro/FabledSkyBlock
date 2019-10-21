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

public class TopBank extends DataCollector {

    private final SkyBlock skyblock;

    public TopBank(SkyBlock skyblock) {
        super("topbank", skyblock.getDescription().getName(), BoardType.DEFAULT, "&bTop Bank", "topbank",
                Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}",
                        ChatColor.WHITE + "Bal: ${amount}", ChatColor.DARK_GRAY + "-=+=-"),
                true, UUID.class);

        this.skyblock = skyblock;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {

        List<Leaderboard> leaderboards = skyblock.getLeaderboardManager().getLeaderboard(Type.Bank);
        Map<UUID, Double> topLevels = new HashMap<>(leaderboards.size());

        for (Leaderboard leaderboard : leaderboards) {
            Visit visit = leaderboard.getVisit();
            topLevels.put(visit.getOwnerUUID(), visit.getBankBalance());
        }

        return LeaderHeadsAPI.sortMap(topLevels);
    }
}
