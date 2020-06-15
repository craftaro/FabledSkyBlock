package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.input.ChatPrompt;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.bank.Transaction;
import com.songoda.skyblock.bank.Type;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.MenuClickRegistry;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

public class Bank {

    private static Bank instance;

    private BankManager bankManager;

    private IslandManager islandManager;

    public static Bank getInstance() {
        return instance == null ? instance = new Bank() : instance;
    }

    public Bank() {
        SkyBlock skyblock = SkyBlock.getInstance();
        MessageManager messageManager = skyblock.getMessageManager();
        islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        bankManager = BankManager.getInstance();
        MenuClickRegistry.getInstance().register((executors) -> {

            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Exit.Displayname", CompatibleMaterial.OAK_FENCE_GATE), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                e.setWillClose(true);
                e.setWillDestroy(false);
            });
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Barrier.Displayname", CompatibleMaterial.BLACK_STAINED_GLASS_PANE), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                e.setWillClose(false);
                e.setWillDestroy(false);
            });
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Deposit.Displayname", CompatibleMaterial.DIRT), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.ENTITY_BAT_TAKEOFF.getSound(), 1.0F, 1.0F);
                //Deposit money
                Island island = islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (island != null) {
                    SelectInputMethod.getInstance().open(player, "Deposit", (action) -> {
                        if (action == InputMethodSelectlistener.InputMethod.CANCELED) {
                            Bukkit.getScheduler().runTask(skyblock, () ->
                                    this.open(player));
                        } else if (action == InputMethodSelectlistener.InputMethod.ALL) {
                            deposit(player, island, EconomyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())));
                        } else {
                            ChatPrompt.showPrompt(skyblock, player, (event) -> {
                                if (event.getMessage().equals(""))
                                    return;

                                double amount;
                                try {
                                    amount = Double.parseDouble(event.getMessage());
                                } catch (NumberFormatException e1) {
                                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short4.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                    return;
                                }
                                deposit(player, island, amount);
                            });
                        }
                    });
                }
                e.setWillClose(true);
                e.setCancelled(false);
            });
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Withdraw.Displayname", CompatibleMaterial.GLISTERING_MELON_SLICE), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.ENTITY_BAT_TAKEOFF.getSound(), 1.0F, 1.0F);
                //Withdraw money
                Island island = null;
                island = islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (island != null) {
                    Island finalIsland = island;
                    SelectInputMethod.getInstance().open(player, "Withdraw", (action) -> {
                        if (action == InputMethodSelectlistener.InputMethod.CANCELED) {
                            Bukkit.getScheduler().runTask(skyblock, () -> {
                                this.open(player);
                            });
                            return;
                        } else if (action == InputMethodSelectlistener.InputMethod.ALL) {
                            withdraw(player, finalIsland, finalIsland.getBankBalance());
                        } else {
                                    ChatPrompt.showPrompt(skyblock, player, (event) -> {
                                                if (event.getMessage().equals(""))
                                                    return;
                                                double amount = 0;
                                                try {
                                                    amount = Double.parseDouble(event.getMessage());
                                                } catch (NumberFormatException e1) {
                                                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short4.Message"));
                                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                                }
                                                withdraw(player, finalIsland, amount);
                                            });
                        }
                    });
                }
                e.setWillClose(true);
                e.setCancelled(false);
            });
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Log.Displayname", CompatibleMaterial.BOOK), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);
                e.setWillClose(false);
                e.setWillDestroy(false);
            });
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Bank.Item.Balance.Displayname", CompatibleMaterial.STICK), (inst, player, e) -> {
                e.setWillClose(false);
                e.setWillDestroy(false);
            });
        });
    }

    public void open(Player player) {
        Island island;
        island = islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));


        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        nInventoryUtil nInv = new nInventoryUtil(player, event -> MenuClickRegistry.getInstance().dispatch(player, event));

        if (island == null) {
            skyblock.getSoundManager().playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);
            skyblock.getMessageManager().sendMessage(player, configLoad.getString("Command.Bank.Unknown"));
            return;
        }

        // Glass panes barriers
        nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(), configLoad.getString("Menu.Bank.Item.Barrier.Displayname"), null, null, null, null), 0, 2, 5, 8,
                1, 2, 3, 5, 6, 7, 9, 11, 12, 14, 15, 17);

        nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(), configLoad.getString("Menu.Bank.Item.Exit.Displayname"),
                configLoad.getStringList("Menu.Bank.Item.Exit.Lore"), null, null, null), 0, 8);

        nInv.addItem(nInv.createItem(CompatibleMaterial.BOOK.getItem(), configLoad.getString("Menu.Bank.Item.Log.Displayname"),
                bankManager.getTransactions(player), null, null, null), 4);

        nInv.addItem(nInv.createItem(CompatibleMaterial.DIRT.getItem(), configLoad.getString("Menu.Bank.Item.Deposit.Displayname"),
                configLoad.getStringList("Menu.Bank.Item.Deposit.Lore"), null, null, null), 10);

        nInv.addItem(nInv.createItem(CompatibleMaterial.GLISTERING_MELON_SLICE.getItem(), configLoad.getString("Menu.Bank.Item.Withdraw.Displayname"),
                configLoad.getStringList("Menu.Bank.Item.Withdraw.Lore"), null, null, null), 16);

        nInv.addItem(nInv.createItem(CompatibleMaterial.STICK.getItem(), configLoad.getString("Menu.Bank.Item.Balance.Displayname"),
                BankManager.getInstance().getBalanceLore(player), null, null, null), 13);


        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configLoad.getString("Menu.Bank.Title"))));
        nInv.setRows(2);

        Bukkit.getServer().getScheduler().runTask(skyblock, nInv::open);
    }

    private void deposit(Player player, Island island, double amt) {
        SkyBlock skyblock = SkyBlock.getInstance();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        // Make sure the amount is positive
        if (amt <= 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short5.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        // If decimals aren't allowed, check for them
        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short6.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                return;
            }
        }

        if (!EconomyManager.hasBalance(player, amt)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        EconomyManager.withdrawBalance(player, amt);
        island.addToBank(amt);
        Transaction t = new Transaction();
        t.player = player;
        t.ammount = (float) amt;
        t.timestamp = Calendar.getInstance().getTime();
        t.action = Type.DEPOSIT;
        bankManager.addTransaction(player, t);
        messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Deposit.Message").replace(
                "%amount%", NumberUtil.formatNumberByDecimal(amt)));
    }

    private void withdraw(Player player, Island island, double amt) {
        SkyBlock skyblock = SkyBlock.getInstance();
        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        // Make sure the amount is positive
        if (amt <= 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short5.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        // If decimals aren't allowed, check for them
        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short6.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                return;
            }
        }

        if (amt > island.getBankBalance()) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short2.Message"));
            return;
        }

        EconomyManager.deposit(player, amt);
        island.removeFromBank(amt);
        Transaction t = new Transaction();
        t.player = player;
        t.ammount = (float) amt;
        t.timestamp = Calendar.getInstance().getTime();
        t.action = Type.WITHDRAW;
        bankManager.addTransaction(player, t);
        messageManager.sendMessage(player, Objects.requireNonNull(configLoad.getString("Command.Island.Bank.Withdraw.Message")).replace(
                "%amount%", NumberUtil.formatNumberByDecimal(amt)));
    }
}
