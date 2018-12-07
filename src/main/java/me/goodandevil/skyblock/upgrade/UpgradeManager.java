package me.goodandevil.skyblock.upgrade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;

public class UpgradeManager {

	private SkyBlock skyblock;
	private Map<Upgrade.Type, List<Upgrade>> upgradeStorage = new HashMap<>();

	public UpgradeManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		for (Upgrade.Type typeList : Upgrade.Type.values()) {
			if (typeList != Upgrade.Type.Size) {
				List<Upgrade> upgrades = new ArrayList<>();

				Upgrade upgrade = new Upgrade(configLoad.getDouble("Upgrades." + typeList.name() + ".Cost"));
				upgrade.setEnabled(configLoad.getBoolean("Upgrades." + typeList.name() + ".Enable"));
				upgrades.add(upgrade);

				upgradeStorage.put(typeList, upgrades);
			}
		}

		if (configLoad.getString("Upgrades.Size") != null) {
			List<Upgrade> upgrades = new ArrayList<>();

			for (String tierList : configLoad.getConfigurationSection("Upgrades.Size").getKeys(false)) {
				if (configLoad.getString("Upgrades.Size." + tierList + ".Value") != null) {
					if (configLoad.getInt("Upgrades.Size." + tierList + ".Value") > 1000) {
						continue;
					}
				}

				upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Size." + tierList + ".Cost"),
						configLoad.getInt("Upgrades.Size." + tierList + ".Value")));

				if (upgrades.size() == 5) {
					break;
				}
			}

			upgradeStorage.put(Upgrade.Type.Size, upgrades);
		}
	}

	public List<Upgrade> getUpgrades(Upgrade.Type type) {
		if (upgradeStorage.containsKey(type)) {
			return upgradeStorage.get(type);
		}

		return null;
	}

	public void addUpgrade(Upgrade.Type type, int value) {
		List<Upgrade> upgrades = new ArrayList<Upgrade>();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.getString("Upgrades.Size") != null) {
			for (String tierList : configLoad.getConfigurationSection("Upgrades.Size").getKeys(false)) {
				upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Size." + tierList + ".Cost"),
						configLoad.getInt("Upgrades.Size." + tierList + ".Value")));

				if (upgrades.size() == 4) {
					break;
				}
			}
		}

		upgrades.add(new Upgrade(0, value));
		configLoad.set("Upgrades.Size", null);

		for (int i = 0; i < upgrades.size(); i++) {
			Upgrade upgrade = upgrades.get(i);
			configLoad.set("Upgrades.Size." + i + ".Value", upgrade.getValue());
			configLoad.set("Upgrades.Size." + i + ".Cost", upgrade.getCost());
		}

		upgradeStorage.put(type, upgrades);

		try {
			configLoad.save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeUpgrade(Upgrade.Type type, double cost, int value) {
		for (Upgrade upgradeList : upgradeStorage.get(type)) {
			if (upgradeList.getCost() == cost && upgradeList.getValue() == value) {
				List<Upgrade> upgrades = upgradeStorage.get(type);
				upgrades.remove(upgradeList);

				Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
				FileConfiguration configLoad = config.getFileConfiguration();

				configLoad.set("Upgrades.Size", null);

				for (int i = 0; i < upgrades.size(); i++) {
					Upgrade upgrade = upgrades.get(i);
					configLoad.set("Upgrades.Size." + i + ".Value", upgrade.getValue());
					configLoad.set("Upgrades.Size." + i + ".Cost", upgrade.getCost());
				}

				try {
					configLoad.save(config.getFile());
				} catch (IOException e) {
					e.printStackTrace();
				}

				return;
			}
		}
	}

	public boolean hasUpgrade(Upgrade.Type type, int value) {
		if (upgradeStorage.containsKey(type)) {
			for (Upgrade upgradeList : upgradeStorage.get(type)) {
				if (upgradeList.getValue() == value) {
					return true;
				}
			}
		}

		return false;
	}
}
