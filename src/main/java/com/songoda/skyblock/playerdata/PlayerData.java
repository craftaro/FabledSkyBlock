package com.songoda.skyblock.playerdata;

import com.eatthepath.uuid.FastUUID;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.Transaction;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.database.exceptions.NoPlayerDataException;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.menus.MenuType;
import com.songoda.skyblock.utils.structure.Area;
import org.apache.commons.lang.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerData {
    
    private final SkyBlock plugin;
    private final UUID uuid;
    private UUID islandUUID;
    private long firstJoin;

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

    //TODO move this to island data
    @Deprecated
    private List<Transaction> transactions;
    private Config playerConfig;

    private boolean scoreboard;
    private String[] texture;
    private long lastOnline;
    private long islandJoinTime;
    private int islandCreationCount;
    private int islandDeleteCount;
    private IslandRole islandRole;

    public PlayerData(UUID playerUUID) {
        this.plugin = SkyBlock.getInstance();
        this.uuid = playerUUID;
        pages = new ArrayList<>();
        confirmationTime = 0;
        area = new Area();
        spiedIslands = new HashSet<>();
        chat = false;
        preview = false;
        transactions = new ArrayList<>();

        switch (plugin.getDataManager().getDatabaseType()) {
            case POSTGRESQL:
            case MARIADB:
            case MYSQL:
            case SQLITE:
                try (Connection connection = plugin.getDatabaseConnector().getConnection()) {
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + SkyBlock.getTable("players") +" WHERE uuid = ?");
                    statement.setString(1, FastUUID.toString(playerUUID));
                    ResultSet data = statement.executeQuery();
                    if (data.next()) {
                        playTime = data.getInt("playTime");
                        firstJoin = data.getLong("joinTime");
                        this.islandRole = IslandRole.valueOf(data.getString("islandRole"));
                        chatSpy = data.getBoolean("chatSpy");
                        byte[] spiedIslands = data.getBytes("spiedIslands");
                        if (spiedIslands != null) {
                            this.spiedIslands.addAll((List<UUID>) SerializationUtils.deserialize(spiedIslands));
                        }
                        this.scoreboard = data.getBoolean("scoreboard");
                        this.texture = new String[]{data.getString("textureSignature"), data.getString("textureValue")};
                        this.islandCreationCount = data.getInt("islandCreationCount");
                        this.islandDeleteCount = data.getInt("islandDeleteCount");
                        this.islandJoinTime = data.getLong("islandJoinTime");
                    } else {
                        throw new NoPlayerDataException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MONGODB:
                throw new RuntimeException("MongoDB is not supported yet");
            //Flatfile
            default:
                playerConfig = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(playerUUID) + ".yml"));
                chatSpy = getConfig().getFileConfiguration().getBoolean("ChatSpy", false);
                if (getConfig().getFileConfiguration().getString("ChatSpiedIslands") != null) {
                    for (String islandUUID : getConfig().getFileConfiguration().getStringList("ChatSpiedIslands")) {
                        spiedIslands.add(FastUUID.parseUUID(islandUUID));
                    }
                }
                this.scoreboard = getConfig().getFileConfiguration().getBoolean("Scoreboard", true);
                String island = getConfig().getFileConfiguration().getString("Island.UUID");
                String islandOwner = getConfig().getFileConfiguration().getString("Island.Owner");
                this.islandRole = IslandRole.valueOf(getConfig().getFileConfiguration().getString("Island.Role", "Member"));
                this.islandUUID = island == null ? null : FastUUID.parseUUID(island);
                this.texture = new String[] { getConfig().getFileConfiguration().getString("Texture.Signature"), getConfig().getFileConfiguration().getString("Texture.Value") };
                //TODO convert from string to timestamp
                this.lastOnline = getConfig().getFileConfiguration().getLong("Statistics.Island.LastOnline");
                this.islandCreationCount = getConfig().getFileConfiguration().getInt("Statistics.Island.CreationCount");
                this.islandDeleteCount = getConfig().getFileConfiguration().getInt("Statistics.Island.DeleteCount");
                this.islandJoinTime = getConfig().getFileConfiguration().getLong("Statistics.Island.Join", 0);
                break;
        }
    }

    @Deprecated
    public PlayerData(Player player) {
        this(player.getUniqueId());
    }

    public int getPage(MenuType type) {
        for(MenuPage menu : pages){
            if(menu.getType().equals(type)){
                return menu.getPage();
            }
        }
        return 1;
    }

    public void setPage(MenuType type, int page) {
        for(MenuPage menu : pages){
            if(menu.getType().equals(type)){
               menu.setPage(page);
               return;
            }
        }
        pages.add(new MenuPage(type, page));
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public UUID getIsland() {
        return islandUUID;
    }

    public void setIsland(UUID islandUUID) {
        this.islandUUID = islandUUID;
    }

    public int getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(int confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public boolean hasConfirmation() {
        return confirmationTime > 0;
    }

    public int getPlaytime() {
        return playTime;
    }

    public void setPlaytime(int playTime) {
        this.playTime = playTime;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }

    public boolean isScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(boolean scoreboard) {
        this.scoreboard = true;
    }
    
    public boolean isPreview() {
        return preview;
    }
    
    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public int getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(int visitTime) {
        this.visitTime = visitTime;
    }

    public long getMemberSince() {
        return islandJoinTime;
    }

    public void setMemberSince(long timestamp) {
        this.islandJoinTime = timestamp;
    }

    public void clearMemberSince() {
        this.islandJoinTime = 0;
    }

    public String[] getTexture() {
        return texture;
    }

    public void setTexture(String signature, String value) {
        this.texture = new String[] { signature, value };
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public void setLastOnline(long timestamp) {
        this.lastOnline = timestamp;
    }
    
    public int getIslandCreationCount() {
        return islandCreationCount;
    }

    public int getIslandDeletionCount() {
        return islandDeleteCount;
    }

    public void setIslandCreationCount(int newNumber) {
        this.islandCreationCount = newNumber;
    }

    public void setIslandDeletionCount(int newNumber) {
        this.islandDeleteCount = newNumber;
    }

    public Area getArea() {
        return area;
    }

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public Object getViewer() {
        return viewer;
    }

    public void setViewer(Object viewer) {
        this.viewer = viewer;
    }

    //ToDo move this to island data
    public void deleteTransactions() {
        Config config = getConfig();
        FileConfiguration configLoad = config.getFileConfiguration();
        configLoad.set("Bank.Transactions",null);
        configLoad.set("Bank.Transactions.Size",0);
        try {
            configLoad.save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO move this to island data
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public synchronized void save() {
        plugin.getDataManager().getDataProvider().savePlayerData(this);
    }

    /**
     * Only used when DatabaseType is FlatFile
     * @return the player's config if DatabaseType is FlatFile otherwise null
     */
    private Config getConfig() {
        return playerConfig;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public boolean isChatSpy() {
        return chatSpy;
    }
    
    public void setChatSpy(boolean chatSpy) {
        this.chatSpy = chatSpy;
    }
    
    public void addChatSpyIsland(UUID uuid) {
        spiedIslands.add(uuid);
    }
    
    public boolean isChatSpyIsland(UUID uuid) {
        return spiedIslands.contains(uuid);
    }
    
    public void removeChatSpyIsland(UUID uuid) {
        spiedIslands.remove(uuid);
    }
    
    public Set<UUID> getChatSpyIslands() {
        return new HashSet<>(spiedIslands);
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
        return spiedIslands.isEmpty();
    }
    
    public void enableGlobalChatSpy() {
        spiedIslands.clear();
    }

    public IslandRole getIslandRole() {
        return islandRole;
    }

    public void setIslandRole(IslandRole islandRole) {
        this.islandRole = islandRole;
    }
}
