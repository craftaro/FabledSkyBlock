package com.craftaro.skyblock.playerdata;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.bank.Transaction;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.confirmation.Confirmation;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.menus.MenuType;
import com.craftaro.skyblock.utils.structure.Area;
import com.eatthepath.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private final SkyBlock plugin;
    private final UUID uuid;
    private UUID islandOwnerUUID;
    private UUID ownershipUUID;

    private final List<MenuPage> pages;
    private int playTime;
    private int visitTime;
    private int confirmationTime;

    private Confirmation confirmation;

    private Object type;
    private Object sort;

    private final Area area;

    private boolean chatSpy;
    private final Set<UUID> spiedIslands;

    private boolean chat;
    private boolean preview;

    private Object viewer;

    private List<Transaction> transactions;

    public PlayerData(Player player) {
        this.plugin = SkyBlock.getPlugin(SkyBlock.class);

        this.uuid = player.getUniqueId();
        this.islandOwnerUUID = null;

        this.pages = new ArrayList<>();

        this.confirmationTime = 0;
        this.playTime = getConfig().getFileConfiguration().getInt("Statistics.Island.Playtime");

        this.area = new Area();

        this.chatSpy = getConfig().getFileConfiguration().getBoolean("ChatSpy", false);
        this.spiedIslands = new HashSet<>();

        if (getConfig().getFileConfiguration().getString("ChatSpiedIslands") != null) {
            for (String islandUUID : getConfig().getFileConfiguration().getStringList("ChatSpiedIslands")) {
                this.spiedIslands.add(FastUUID.parseUUID(islandUUID));
            }
        }

        this.chat = false;
        this.preview = false;
        this.transactions = new ArrayList<>();
        FileConfiguration configLoad = getConfig().getFileConfiguration();
        for (int i = 0; i < configLoad.getInt("Bank.Transactions.Size"); i++) {
            Transaction t = new Transaction();
            t.action = Transaction.Type.valueOf(configLoad.getString("Bank.Transactions." + i + ".Action"));
            t.amount = Float.parseFloat(Objects.requireNonNull(configLoad.getString("Bank.Transactions." + i + ".Amount")));
            t.player = Bukkit.getOfflinePlayer(FastUUID.parseUUID(Objects.requireNonNull(configLoad.getString("Bank.Transactions." + i + ".Player"))));
            Date d = new Date();
            d.setTime(configLoad.getLong("Bank.Transactions." + i + ".Date"));
            t.timestamp = d;
            String visibility = configLoad.getString("Bank.Transactions." + i + ".Visibility");
            if (visibility != null) {
                t.visibility = Transaction.Visibility.valueOf(visibility);
            } else {
                t.visibility = Transaction.Visibility.USER; // Defaulting this as it's a new field
            }
            this.transactions.add(t);
        }
    }

    public int getPage(MenuType type) {
        for (MenuPage menu : this.pages) {
            if (menu.getType() == type) {
                return menu.getPage();
            }
        }
        return 1;
    }

    public void setPage(MenuType type, int page) {
        for (MenuPage menu : this.pages) {
            if (menu.getType() == type) {
                menu.setPage(page);
                return;
            }
        }
        this.pages.add(new MenuPage(type, page));
    }

    public Object getType() {
        return this.type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getSort() {
        return this.sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public UUID getIsland() {
        return this.islandOwnerUUID;
    }

    public void setIsland(UUID islandOwnerUUID) {
        this.islandOwnerUUID = islandOwnerUUID;
    }

    public UUID getOwnership() {
        return this.ownershipUUID;
    }

    public void setOwnership(UUID ownershipUUID) {
        this.ownershipUUID = ownershipUUID;
    }

    public int getConfirmationTime() {
        return this.confirmationTime;
    }

    public void setConfirmationTime(int confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public Confirmation getConfirmation() {
        return this.confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public boolean hasConfirmation() {
        return this.confirmationTime > 0;
    }

    public int getPlaytime() {
        return this.playTime;
    }

    public void setPlaytime(int playTime) {
        this.playTime = playTime;
    }

    public boolean isScoreboard() {
        return getConfig().getFileConfiguration().getBoolean("Scoreboard", true);
    }

    public void setScoreboard(boolean scoreboard) {
        getConfig().getFileConfiguration().set("Scoreboard", scoreboard);
    }

    public boolean isPreview() {
        return this.preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public int getVisitTime() {
        return this.visitTime;
    }

    public void setVisitTime(int visitTime) {
        this.visitTime = visitTime;
    }

    public String getMemberSince() {
        return getConfig().getFileConfiguration().getString("Statistics.Island.Join");
    }

    public void setMemberSince(String date) {
        getConfig().getFileConfiguration().set("Statistics.Island.Join", date);
    }

    public UUID getOwner() {
        String islandOwnerUUID = getConfig().getFileConfiguration().getString("Island.Owner");
        return (islandOwnerUUID == null) ? null : FastUUID.parseUUID(islandOwnerUUID);
    }

    public void setOwner(UUID islandOwnerUUID) {
        if (islandOwnerUUID == null) {
            getConfig().getFileConfiguration().set("Island.Owner", null);
        } else {
            getConfig().getFileConfiguration().set("Island.Owner", islandOwnerUUID.toString());
        }
    }

    public String[] getTexture() {
        FileConfiguration configLoad = getConfig().getFileConfiguration();

        return new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
    }

    public void setTexture(String signature, String value) {
        getConfig().getFileConfiguration().set("Texture.Signature", signature);
        getConfig().getFileConfiguration().set("Texture.Value", value);
    }

    public String getLastOnline() {
        return getConfig().getFileConfiguration().getString("Statistics.Island.LastOnline");
    }

    public void setLastOnline(String date) {
        getConfig().getFileConfiguration().set("Statistics.Island.LastOnline", date);
    }

    public long getIslandCreationCount() {
        return getConfig().getFileConfiguration().getLong("Statistics.Island.IslandCreationCount");
    }

    public long getIslandDeletionCount() {
        return getConfig().getFileConfiguration().getLong("Statistics.Island.IslandDeleteCount");
    }

    public void setIslandCreationCount(long newNumber) {
        getConfig().getFileConfiguration().set("Statistics.Island.IslandCreationCount", newNumber);
    }

    public void setIslandDeletionCount(long newNumber) {
        getConfig().getFileConfiguration().set("Statistics.Island.IslandDeleteCount", newNumber);
    }


    public Area getArea() {
        return this.area;
    }

    public boolean isChat() {
        return this.chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public Object getViewer() {
        return this.viewer;
    }

    public void setViewer(Object viewer) {
        this.viewer = viewer;
    }

    public void deleteTransactions() {
        FileManager.Config config = getConfig();
        FileConfiguration configLoad = config.getFileConfiguration();
        configLoad.set("Bank.Transactions", null);
        configLoad.set("Bank.Transactions.Size", 0);
        try {
            configLoad.save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void save() {
        this.transactions = this.plugin.getBankManager().getTransactionList(getPlayerUUID());
        FileManager.Config config = getConfig();
        FileConfiguration configLoad = config.getFileConfiguration();
        configLoad.set("Statistics.Island.Playtime", getPlaytime());
        if (this.transactions != null) {
            configLoad.set("Bank.Transactions.Size", this.transactions.size());
            for (int i = 0; i < this.transactions.size(); i++) {
                Transaction t = this.transactions.get(i);
                configLoad.set("Bank.Transactions." + i + ".Action", t.action.name());
                configLoad.set("Bank.Transactions." + i + ".Amount", t.amount);
                configLoad.set("Bank.Transactions." + i + ".Player", t.player.getUniqueId().toString());
                configLoad.set("Bank.Transactions." + i + ".Date", t.timestamp.getTime());
                configLoad.set("Bank.Transactions." + i + ".Visibility", t.visibility.name());
            }
        } else {
            configLoad.set("Bank.Transactions.Size", 0);
        }

        configLoad.set("ChatSpy", this.chatSpy);
        List<String> tempSpiedIslands = new ArrayList<>();
        for (UUID uuid : this.spiedIslands) {
            tempSpiedIslands.add(FastUUID.toString(uuid));
        }
        configLoad.set("ChatSpiedIslands", tempSpiedIslands);

        try {
            configLoad.save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private FileManager.Config getConfig() {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);
        return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(this.uuid) + ".yml"));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public boolean isChatSpy() {
        return this.chatSpy;
    }

    public void setChatSpy(boolean chatSpy) {
        this.chatSpy = chatSpy;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }

    public void addChatSpyIsland(UUID uuid) {
        this.spiedIslands.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }

    public boolean isChatSpyIsland(UUID uuid) {
        return this.spiedIslands.contains(uuid);
    }

    public void removeChatSpyIsland(UUID uuid) {
        this.spiedIslands.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }

    public Set<UUID> getChatSpyIslands() {
        return new HashSet<>(this.spiedIslands);
    }

    public void addChatSpyIsland(Island island) {
        this.addChatSpyIsland(island.getOwnerUUID());
    }

    public boolean isChatSpyIsland(Island island) {
        return this.isChatSpyIsland(island.getOwnerUUID());
    }

    public void removeChatSpyIsland(Island island) {
        this.removeChatSpyIsland(island.getOwnerUUID());
    }

    public boolean isGlobalChatSpy() {
        return this.spiedIslands.isEmpty();
    }

    public void enableGlobalChatSpy() {
        this.spiedIslands.clear();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }
}
