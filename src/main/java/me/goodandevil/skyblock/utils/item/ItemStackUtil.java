package me.goodandevil.skyblock.utils.item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;

import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.utils.version.NMSUtil;

public final class ItemStackUtil {

	public static ItemStack deserializeItemStack(String data) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
		DataInputStream dataInputStream = new DataInputStream(inputStream);

		ItemStack itemStack = null;

		try {
			Class<?> NBTTagCompoundClass = NMSUtil.getNMSClass("NBTTagCompound");
			Class<?> NMSItemStackClass = NMSUtil.getNMSClass("ItemStack");
			Object NBTTagCompound = NMSUtil.getNMSClass("NBTCompressedStreamTools")
					.getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
			Object craftItemStack;

			if (NMSUtil.getVersionNumber() > 12) {
				craftItemStack = NMSItemStackClass.getMethod("a", NBTTagCompoundClass).invoke(null, NBTTagCompound);
			} else if (NMSUtil.getVersionNumber() > 10) {
				craftItemStack = NMSItemStackClass.getConstructor(NBTTagCompoundClass).newInstance(NBTTagCompound);
			} else {
				craftItemStack = NMSItemStackClass.getMethod("createStack", NBTTagCompoundClass).invoke(null,
						NBTTagCompound);
			}

			itemStack = (ItemStack) NMSUtil.getCraftClass("inventory.CraftItemStack")
					.getMethod("asBukkitCopy", NMSItemStackClass).invoke(null, craftItemStack);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemStack;
	}

	public static String serializeItemStack(ItemStack item) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(outputStream);

		try {
			Class<?> NBTTagCompoundClass = NMSUtil.getNMSClass("NBTTagCompound");
			Constructor<?> nbtTagCompoundConstructor = NBTTagCompoundClass.getConstructor();
			Object NBTTagCompound = nbtTagCompoundConstructor.newInstance();
			Object NMSItemStackClass = NMSUtil.getCraftClass("inventory.CraftItemStack")
					.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			NMSUtil.getNMSClass("ItemStack").getMethod("save", NBTTagCompoundClass).invoke(NMSItemStackClass,
					NBTTagCompound);
			NMSUtil.getNMSClass("NBTCompressedStreamTools").getMethod("a", NBTTagCompoundClass, DataOutput.class)
					.invoke(null, NBTTagCompound, dataOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new BigInteger(1, outputStream.toByteArray()).toString(32);
	}
}
