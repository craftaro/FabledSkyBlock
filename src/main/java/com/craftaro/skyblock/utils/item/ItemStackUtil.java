package com.craftaro.skyblock.utils.item;

import com.craftaro.core.compatibility.ClassMapping;
import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.MethodMapping;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.NMSUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;

public class ItemStackUtil {
    private static final boolean isAbove1_16_R1 = MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_16)
            && !ServerVersion.getServerVersionString().equals("v1_16_R1");

    public static ItemStack deserializeItemStack(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        ItemStack itemStack = null;

        try {
            Class<?> NBTTagCompoundClass = ClassMapping.NBT_TAG_COMPOUND.getClazz();
            Class<?> NMSItemStackClass = ClassMapping.ITEM_STACK.getClazz();
            Object NBTTagCompound = isAbove1_16_R1 ? ClassMapping.NBT_COMPRESSED_STREAM_TOOLS.getClazz()
                    .getMethod("a", DataInput.class).invoke(null, dataInputStream)
                    : ClassMapping.NBT_COMPRESSED_STREAM_TOOLS.getClazz()
                    .getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
            Object craftItemStack;

            assert NMSItemStackClass != null;
            if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_13)) {
                craftItemStack = NMSItemStackClass.getMethod("a", NBTTagCompoundClass).invoke(null, NBTTagCompound);
            } else if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_11)) {
                craftItemStack = NMSItemStackClass.getConstructor(NBTTagCompoundClass).newInstance(NBTTagCompound);
            } else {
                craftItemStack = NMSItemStackClass.getMethod("createStack", NBTTagCompoundClass).invoke(null,
                        NBTTagCompound);
            }

            itemStack = (ItemStack) NMSUtils.getCraftClass("inventory.CraftItemStack")
                    .getMethod("asBukkitCopy", NMSItemStackClass).invoke(null, craftItemStack);

            // TODO: This method of serialization has some issues. Not all the names are the same between versions
            // Make an exception for reeds/melon, they NEED to load in the island chest
            // This code is here SPECIFICALLY to get the default.structure to load properly in all versions
            // Other structures people make NEED to be saved from the version that they will be using so everything loads properly
            if (itemStack.getType() == Material.AIR) {
                if (NBTTagCompound.toString().equals("{id:\"minecraft:sugar_cane\",Count:1b}")) {
                    itemStack = XMaterial.SUGAR_CANE.parseItem();
                } else if (NBTTagCompound.toString().equals("{id:\"minecraft:melon_slice\",Count:1b}")) {
                    itemStack = XMaterial.MELON_SLICE.parseItem();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return itemStack;
    }

    public static String serializeItemStack(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        try {
            Class<?> NBTTagCompoundClass = ClassMapping.NBT_TAG_COMPOUND.getClazz();
            Constructor<?> nbtTagCompoundConstructor = NBTTagCompoundClass.getConstructor();
            Object NBTTagCompound = nbtTagCompoundConstructor.newInstance();

            Object nmsItemStack = MethodMapping.CB_ITEM_STACK__AS_NMS_COPY
                    .getMethod(ClassMapping.CRAFT_ITEM_STACK.getClazz())
                    .invoke(null, item);

            MethodMapping.ITEM_STACK__SAVE
                    .getMethod(ClassMapping.ITEM_STACK.getClazz())
                    .invoke(nmsItemStack, NBTTagCompound);

            ClassMapping.NBT_COMPRESSED_STREAM_TOOLS.getClazz().getMethod("a", NBTTagCompoundClass, DataOutput.class)
                    .invoke(null, NBTTagCompound, dataOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }
}
