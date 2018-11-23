package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Leaderboard implements Listener {

    private static Leaderboard instance;

    public static Leaderboard getInstance(){
        if(instance == null) {
            instance = new Leaderboard();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
    	FileManager fileManager = plugin.getFileManager();
    	
    	Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		Viewer viewer = (Viewer) playerDataManager.getPlayerData(player).getViewer();
		
		InventoryUtil inv;
		
		if (viewer.getType() == Viewer.Type.Browse) {
			inv = new InventoryUtil(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Title")), InventoryType.HOPPER, 1);
			inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Item.Exit.Displayname"), null, null, null, null), 0, 4);
			inv.addItem(inv.createItem(new ItemStack(Material.DIAMOND), configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Displayname").replace("%leaderboard", Viewer.Type.Level.name()), configLoad.getStringList("Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Lore"), inv.createItemLoreVariable(new String[] { "%leaderboard#" + Viewer.Type.Level.name() }), null, null), 1);
			inv.addItem(inv.createItem(new ItemStack(Material.EMERALD), configLoad.getString("Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Displayname").replace("%leaderboard", Viewer.Type.Votes.name()), configLoad.getStringList("Menu.Leaderboard." + viewer.getType().name() + ".Item.Leaderboard.Lore"), inv.createItemLoreVariable(new String[] { "%leaderboard#" + Viewer.Type.Votes.name() }), null, null), 3);
		} else {
			inv = new InventoryUtil(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard.Leaderboard.Title").replace("%leaderboard", viewer.getType().name())), null, 6);
			
			if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
				inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname"), null, null, null, null), 0, 8);
			} else {
				inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname"), null, null, null, null), 0, 8);				
			}
			
			List<me.goodandevil.skyblock.leaderboard.Leaderboard> leaderboardIslands = plugin.getLeaderboardManager().getLeaderboard(me.goodandevil.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().name()));
			
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
				
				for (String itemLoreList : configLoad.getStringList("Menu.Leaderboard.Leaderboard.Item.Island." + viewer.getType().name() + ".Lore")) {
					if (itemLoreList.contains("%signature")) {
						if (visit.getSiganture() == null || visit.getSiganture().size() == 0) {
							itemLore.add(configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Word.Empty"));
						} else {
							for (String signatureList : visit.getSiganture()) {
								itemLore.add(signatureList);
							}
						}
					} else {
						itemLore.add(itemLoreList);
					}
				}
				
				inv.addItem(inv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]), configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Displayname").replace("%position", "" + (leaderboard.getPosition() + 1)), itemLore, inv.createItemLoreVariable(new String[] { "%position#" + (leaderboard.getPosition() + 1), "%owner#" + playerName, "%level#" + visit.getLevel(), "%votes#" + visit.getVoters().size(), "%members#" + visit.getMembers() }), null, null), itemSlot);
			}
			
			int[] itemSlots = new int[] { 13, 21, 22, 23, 29, 31, 33, 37, 40, 43 };
			
			for (int i = 0; i < itemSlots.length; i++) {
				if (inv.getInventory().getItem(itemSlots[i]) == null) {
					inv.addItem(inv.createItem(SkullUtil.create("gi+wnQt/y4Z6E9rn65iDWmt8vUOM2WXY66XvtydqDJZTzwgFrjVcx2c5YwdzvtOIRtiX2nZt4n2uWesUFKb59xS24YWbxCDXnalHhCpPFcIP58SQbCm9AYp3UPzkcRNWzuV4BddrS608QQZGyIFOUaLPOPasGITZu51VLcOKcTyFOCKu1QE2yRo1orTH8bWfdpE769BB/VYGdny0qJtm1amc12wGiVifMJRutZmYo2ZdA0APhIJVaNsPppNESVcbeBCvk60l4QK43C/p98/QEe5U6UJ6Z6N01pBQcswubMu8lCuPLasep+vX3v2K+Ui9jnTQNreGNIZPWVjf6V1GH4xMbbUVQJsoPdcaXG855VdzyoW+kyHdWYEojSn0qAY/moH6JCLnx6PLCv9mITSvOIUHq8ITet0M7Z9KALY5s6eg6VdA8TvClRy2TTm9tIRt//TJo5JxBoTYujawGNSR7ryODj2UEDQ2xOyWSagxAXZpispdrO5jHxRmBZUwX9vxnAp+CNWxifpu9sINJTlqYsT/KlGOJQC483gv5B6Nm5VBB1DRFmQkohzO6Wc2eDixgEbaU795GlLxrNaFfNjVH6Bwr1e7df2H3nE0P0bexs4wYdWplijn4gPyHwjT2LDBPGFQK3Vo2SlaXfPYbkIHX21c9qaz3eWHpLEXUBQfnWc=", "eyJ0aW1lc3RhbXAiOjE1MzE3MTcxNjY3MDAsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzNGUwNjNjYWZiNDY3YTVjOGRlNDNlYzc4NjE5Mzk5ZjM2OWY0YTUyNDM0ZGE4MDE3YTk4M2NkZDkyNTE2YTAifX19"), configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Empty.Displayname").replace("%position", "" + (i + 1)), configLoad.getStringList("Menu.Leaderboard.Leaderboard.Item.Empty.Lore"), inv.createItemLoreVariable(new String[] { "%position#" + (i + 1) }), null, null), itemSlots[i]);
				}
			}
		}
		
		player.openInventory(inv.getInventory());
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
			SoundManager soundManager = plugin.getSoundManager();
			FileManager fileManager = plugin.getFileManager();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard." + Viewer.Type.Browse.name() + ".Title")))) {
				event.setCancelled(true);
				
				if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard." + Viewer.Type.Browse.name() + ".Item.Exit.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		    		player.closeInventory();
				} else if ((event.getCurrentItem().getType() == Material.DIAMOND) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard." + Viewer.Type.Browse.name() + ".Item.Leaderboard.Displayname").replace("%leaderboard", Viewer.Type.Level.name()))))) {
					playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Level));
					open(player);
					soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				} else if ((event.getCurrentItem().getType() == Material.EMERALD) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard." + Viewer.Type.Browse.name() + ".Item.Leaderboard.Displayname").replace("%leaderboard", Viewer.Type.Votes.name()))))) {
					playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Votes));
					open(player);
					soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				}
			} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard.Leaderboard.Title").replace("%leaderboard", Viewer.Type.Level.name()))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard.Leaderboard.Title").replace("%leaderboard", Viewer.Type.Votes.name())))) {
				event.setCancelled(true);
				
				if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())) {
					if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname")))) {
						soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();
					} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname")))) {
						if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
							playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.Browse));
							open(player);
							soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
						} else {
							soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
							player.closeInventory();
						}
						
						return;
					}
				}
				
				soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
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
    		
    		Browse,
    		Level,
    		Votes;
    		
    	}
    }
}
