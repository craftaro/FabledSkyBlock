package me.goodandevil.skyblock.utils.item;

import org.bukkit.entity.Player;

import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;

public class ExampleInventory {

	public void open(Player player) {
		nInventoryUtil nInventoryUtil = new nInventoryUtil(player, "Test", null, 1*9, new ClickEventHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		
		nInventoryUtil.open();
	}
}
