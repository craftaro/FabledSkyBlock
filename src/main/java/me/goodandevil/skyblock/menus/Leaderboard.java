package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.goodandevil.skyblock.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Leaderboard {

	private static Leaderboard instance;

	public static Leaderboard getInstance() {
		if (instance == null) {
			instance = new Leaderboard();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (playerDataManager.hasPlayerData(player)) {
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			Viewer viewer = (Viewer) playerDataManager.getPlayerData(player).getViewer();

			if (viewer.getType() == Viewer.Type.Browse) {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							ItemStack is = event.getItem();

							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Menu.Leaderboard." + Viewer.Type.Browse.name()
															+ ".Item.Exit.Displayname"))))) {
								soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);

								return;
							} else if ((is.getType() == Material.DIAMOND) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&',
													configLoad
															.getString("Menu.Leaderboard." + Viewer.Type.Browse.name()
																	+ ".Item.Leaderboard.Displayname")
															.replace("%leaderboard", Viewer.Type.Level.name()))))) {
								playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Level));
							} else if ((is.getType() == Material.GOLD_INGOT) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											configLoad
													.getString("Menu.Leaderboard." + Viewer.Type.Browse.name()
															+ ".Item.Leaderboard.Displayname")
													.replace("%leaderboard", Viewer.Type.Bank.name()))))) {
								playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Bank));
							} else if ((is.getType() == Material.EMERALD) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&',
													configLoad
															.getString("Menu.Leaderboard." + Viewer.Type.Browse.name()
																	+ ".Item.Leaderboard.Displayname")
															.replace("%leaderboard", Viewer.Type.Votes.name()))))) {
								playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Votes));
							}

							soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
						}
					}
				});

				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Item.Exit.Displayname"),
						null, null, null, null), 0, 4);
				nInv.addItem(
						nInv.createItem(new ItemStack(Material.DIAMOND), configLoad
								.getString(
										"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Displayname")
								.replace("%leaderboard", Viewer.Type.Level.name()),
								configLoad.getStringList(
										"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Lore"),
								new Placeholder[] { new Placeholder("%leaderboard", Viewer.Type.Level.name()) }, null,
								null),
						1);
				nInv.addItem(
						nInv.createItem(new ItemStack(Material.GOLD_INGOT), configLoad
										.getString(
												"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Displayname")
										.replace("%leaderboard", Viewer.Type.Bank.name()),
								configLoad.getStringList(
										"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Lore"),
								new Placeholder[] { new Placeholder("%leaderboard", Viewer.Type.Bank.name()) }, null,
								null),
						2);
				nInv.addItem(
						nInv.createItem(new ItemStack(Material.EMERALD), configLoad
								.getString(
										"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Displayname")
								.replace("%leaderboard", Viewer.Type.Votes.name()),
								configLoad.getStringList(
										"Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Lore"),
								new Placeholder[] { new Placeholder("%leaderboard", Viewer.Type.Votes.name()) }, null,
								null),
						3);

				nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
						configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Title")));
				nInv.setType(InventoryType.HOPPER);

				Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
					@Override
					public void run() {
						nInv.open();
					}
				});
			} else {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							ItemStack is = event.getItem();

							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())) {
								if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname")))) {
									soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
								} else if (is.getItemMeta().getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad
												.getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname")))) {
									if (skyblock.getFileManager()
											.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
											.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
										playerDataManager.getPlayerData(player)
												.setViewer(new Viewer(Viewer.Type.Browse));
										soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
									} else {
										soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
									}

									return;
								}
							}

							int clickedSlot = event.getSlot();
							int leaderboardPosition = -1;

							if (clickedSlot == 13) {
								leaderboardPosition = 0;
							} else if (clickedSlot == 21) {
								leaderboardPosition = 1;
							} else if (clickedSlot == 22) {
								leaderboardPosition = 2;
							} else if (clickedSlot == 23) {
								leaderboardPosition = 3;
							} else if (clickedSlot == 29) {
								leaderboardPosition = 4;
							} else if (clickedSlot == 31) {
								leaderboardPosition = 5;
							} else if (clickedSlot == 33) {
								leaderboardPosition = 6;
							} else if (clickedSlot == 37) {
								leaderboardPosition = 7;
							} else if (clickedSlot == 40) {
								leaderboardPosition = 8;
							} else if (clickedSlot == 43) {
								leaderboardPosition = 9;
							}

							if (leaderboardPosition != -1) {
								List<me.goodandevil.skyblock.leaderboard.Leaderboard> leaderboardIslands = skyblock
										.getLeaderboardManager().getLeaderboard(
												me.goodandevil.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().name()));

								if (leaderboardIslands.size() > leaderboardPosition) {
									me.goodandevil.skyblock.leaderboard.Leaderboard leaderboard = leaderboardIslands.get(leaderboardPosition);
									me.goodandevil.skyblock.visit.Visit visit = leaderboard.getVisit();

									OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
									Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> Bukkit.dispatchCommand(player, "island teleport " + offlinePlayer.getName()));
								}

								event.setWillClose(false);
								event.setWillDestroy(false);

								return;
							}

							soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						}
					}
				});

				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
						.getBoolean("Island.Visitor.Vote")) {
					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname"), null, null,
							null, null), 0, 8);
				} else {
					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname"), null, null,
							null, null), 0, 8);
				}

				List<me.goodandevil.skyblock.leaderboard.Leaderboard> leaderboardIslands = skyblock
						.getLeaderboardManager().getLeaderboard(
								me.goodandevil.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().name()));

				for (int i = 0; i < leaderboardIslands.size(); i++) {
					me.goodandevil.skyblock.leaderboard.Leaderboard leaderboard = leaderboardIslands.get(i);
					me.goodandevil.skyblock.visit.Visit visit = leaderboard.getVisit();

					int itemSlot = 0;

					String playerName;
					String[] playerTexture;

					Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());

					if (targetPlayer == null) {
						OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
						playerName = offlinePlayer.getName();
						playerTexture = offlinePlayer.getTexture();
					} else {
						playerName = targetPlayer.getName();
						playerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
					}

					if (leaderboard.getPosition() == 0) {
						itemSlot = 13;
					} else if (leaderboard.getPosition() == 1) {
						itemSlot = 21;
					} else if (leaderboard.getPosition() == 2) {
						itemSlot = 22;
					} else if (leaderboard.getPosition() == 3) {
						itemSlot = 23;
					} else if (leaderboard.getPosition() == 4) {
						itemSlot = 29;
					} else if (leaderboard.getPosition() == 5) {
						itemSlot = 31;
					} else if (leaderboard.getPosition() == 6) {
						itemSlot = 33;
					} else if (leaderboard.getPosition() == 7) {
						itemSlot = 37;
					} else if (leaderboard.getPosition() == 8) {
						itemSlot = 40;
					} else if (leaderboard.getPosition() == 9) {
						itemSlot = 43;
					}

					List<String> itemLore = new ArrayList<>();

					for (String itemLoreList : configLoad.getStringList(
							"Menu.Leaderboard.Leaderboard.Item.Island." + viewer.getType().name() + ".Lore")) {
						if (itemLoreList.contains("%signature")) {
							if (visit.getSiganture() == null || visit.getSiganture().size() == 0) {
								itemLore.add(
										configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Word.Empty"));
							} else {
								for (String signatureList : visit.getSiganture()) {
									itemLore.add(signatureList);
								}
							}
						} else {
							itemLore.add(itemLoreList);
						}
					}

					nInv.addItem(
							nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]),
									configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Displayname")
											.replace(
													"%owner", playerName)
											.replace("%position", "" + (leaderboard.getPosition() + 1)),
									itemLore,
									new Placeholder[] {
											new Placeholder("%position", "" + (leaderboard.getPosition() + 1)),
											new Placeholder("%owner", playerName),
											new Placeholder("%level", "" + visit.getLevel().getLevel()),
											new Placeholder("%balance", NumberUtil.formatNumberByDecimal(visit.getBankBalance())),
											new Placeholder("%votes", "" + visit.getVoters().size()),
											new Placeholder("%members", "" + visit.getMembers()) },
									null, null),
							itemSlot);
				}

				int[] itemSlots = new int[] { 13, 21, 22, 23, 29, 31, 33, 37, 40, 43 };

				for (int i = 0; i < itemSlots.length; i++) {
					if (!nInv.getItems().containsKey(itemSlots[i])) {
						nInv.addItem(nInv.createItem(SkullUtil.create(
								"gi+wnQt/y4Z6E9rn65iDWmt8vUOM2WXY66XvtydqDJZTzwgFrjVcx2c5YwdzvtOIRtiX2nZt4n2uWesUFKb59xS24YWbxCDXnalHhCpPFcIP58SQbCm9AYp3UPzkcRNWzuV4BddrS608QQZGyIFOUaLPOPasGITZu51VLcOKcTyFOCKu1QE2yRo1orTH8bWfdpE769BB/VYGdny0qJtm1amc12wGiVifMJRutZmYo2ZdA0APhIJVaNsPppNESVcbeBCvk60l4QK43C/p98/QEe5U6UJ6Z6N01pBQcswubMu8lCuPLasep+vX3v2K+Ui9jnTQNreGNIZPWVjf6V1GH4xMbbUVQJsoPdcaXG855VdzyoW+kyHdWYEojSn0qAY/moH6JCLnx6PLCv9mITSvOIUHq8ITet0M7Z9KALY5s6eg6VdA8TvClRy2TTm9tIRt//TJo5JxBoTYujawGNSR7ryODj2UEDQ2xOyWSagxAXZpispdrO5jHxRmBZUwX9vxnAp+CNWxifpu9sINJTlqYsT/KlGOJQC483gv5B6Nm5VBB1DRFmQkohzO6Wc2eDixgEbaU795GlLxrNaFfNjVH6Bwr1e7df2H3nE0P0bexs4wYdWplijn4gPyHwjT2LDBPGFQK3Vo2SlaXfPYbkIHX21c9qaz3eWHpLEXUBQfnWc=",
								"eyJ0aW1lc3RhbXAiOjE1MzE3MTcxNjY3MDAsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzNGUwNjNjYWZiNDY3YTVjOGRlNDNlYzc4NjE5Mzk5ZjM2OWY0YTUyNDM0ZGE4MDE3YTk4M2NkZDkyNTE2YTAifX19"),
								configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Empty.Displayname")
										.replace("%position", "" + (i + 1)),
								configLoad.getStringList("Menu.Leaderboard.Leaderboard.Item.Empty.Lore"),
								new Placeholder[] { new Placeholder("%position", "" + (i + 1)) }, null, null),
								itemSlots[i]);
					}
				}

				nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
						configLoad.getString("Menu.Leaderboard.Leaderboard.Title").replace("%leaderboard",
								viewer.getType().name())));
				nInv.setRows(6);

				Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
			}
		}
	}

	public static class Viewer {

		private Type type;

		public Viewer(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public enum Type {

			Browse, Level, Bank, Votes

		}
	}
}
