package com.songoda.skyblock.utils;

import com.google.common.collect.Lists;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.utils.NMSUtils;
import com.songoda.skyblock.SkyBlock;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;

public final class SignMenuFactory {

    private final Plugin plugin;

    private static SignMenuFactory instance;

    private Class signPacket = NMSUtils.getNMSClass("PacketPlayInUpdateSign");

    private SignMenuFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    public static SignMenuFactory getInstance() {
        return instance == null ? instance = new SignMenuFactory(SkyBlock.getInstance()) : instance;
    }

    public Menu newMenu() {
        return new Menu(Lists.newArrayList("","","",""));
    }

    private void injectPlayer(Player player,Menu m) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg.getClass().equals(signPacket)) {
                    String[] lines;
                    Method getLines = msg.getClass().getMethod("c");
                    if (getLines == null) {
                        return;
                    }
                    lines = (String[])getLines.invoke(msg);

                    player.sendBlockChange(m.position, Material.AIR.createBlockData());
                    boolean success = m.response.test(player, lines);

                    if (!success && m.opensOnFail()) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> m.open(player), 2L);
                    }
                    removePlayer(player);
                }
                super.channelRead(ctx, msg);
            }
        };


        Object handle = NMSUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
        Channel channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addBefore("packet_handler",player.getName(),channelDuplexHandler);
    }

    private void removePlayer(Player player) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object handle = NMSUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
        Channel channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
        ChannelPipeline pipeline = channel.pipeline();
        channel.eventLoop().submit(() -> {
            pipeline.remove(player.getName());
        });
    }

    public class Menu {

        private final List<String> text;

        private BiPredicate<Player, String[]> response;
        private boolean reopenIfFail;

        private Location position;

        Menu(List<String> text) {
            this.text = text;
        }

        boolean opensOnFail() {
            return this.reopenIfFail;
        }

        public Menu reopenIfFail() {
            this.reopenIfFail = true;
            return this;
        }

        public Menu response(BiPredicate<Player, String[]> response) {
            this.response = response;
            return this;
        }

        public void open(Player player) {
            Objects.requireNonNull(player, "player");
            this.position = player.getLocation().getBlock().getLocation();

            player.sendBlockChange(this.position, CompatibleMaterial.OAK_SIGN.getBlockMaterial().createBlockData());

            Class openSign = NMSUtils.getNMSClass("PacketPlayOutOpenSignEditor");
            Class blockPosition = NMSUtils.getNMSClass("BlockPosition");

            Object openSignPacket = null;
            try {
                Object blockPos = blockPosition.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(position.getBlockX(), position.getBlockY(), position.getBlockZ());
                openSignPacket = openSign.getConstructor(blockPosition).newInstance(blockPos);
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            NMSUtils.sendPacket(player,openSignPacket);

            try {
                injectPlayer(player,this);
            } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public List<String> getText() {
            return text;
        }
    }
}
