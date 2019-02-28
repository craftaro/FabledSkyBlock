package me.goodandevil.skyblock.leaderboard.leaderheads;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.leaderboard.Leaderboard;
import me.goodandevil.skyblock.leaderboard.Leaderboard.Type;
import me.goodandevil.skyblock.visit.Visit;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;

public class TopVotes extends DataCollector {

	private final SkyBlock skyblock;

	public TopVotes(SkyBlock skyblock) {
		super("topvotes", skyblock.getDescription().getName(), BoardType.DEFAULT, "&bTop Votes", "topvotes",
				Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}",
						ChatColor.WHITE + "{amount} Votes", ChatColor.DARK_GRAY + "-=+=-"),
				true, UUID.class);

		this.skyblock = skyblock;
	}

	@Override
	public List<Entry<?, Double>> requestAll() {
		Map<UUID, Double> topLevels = new HashMap<>();

		List<Leaderboard> leaderboards = skyblock.getLeaderboardManager().getLeaderboard(Type.Votes);

		for (int i = 0; i < leaderboards.size(); i++) {
			Leaderboard leaderboard = leaderboards.get(i);
			Visit visit = leaderboard.getVisit();
			topLevels.put(visit.getOwnerUUID(), (double) visit.getVoters().size());
		}

		return LeaderHeadsAPI.sortMap(topLevels);
	}
}
