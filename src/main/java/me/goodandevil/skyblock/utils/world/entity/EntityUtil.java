package me.goodandevil.skyblock.utils.world.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

import me.goodandevil.skyblock.utils.item.ItemStackUtil;
import me.goodandevil.skyblock.utils.item.MaterialUtil;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.world.block.BlockDegreesType;

@SuppressWarnings("deprecation")
public final class EntityUtil {

	public static EntityData convertEntityToEntityData(Entity entity, int x, int y, int z) {
		EntityData entityData = new EntityData(entity.getType().toString(), x, y, z, entity.getCustomName(),
				entity.isCustomNameVisible(), entity.getFireTicks(), entity.getTicksLived());
		entityData.setVersion(NMSUtil.getVersionNumber());

		if (entity instanceof ArmorStand) {
			ArmorStand armorStand = (ArmorStand) entity;
			entityData.setArms(armorStand.hasArms());

			if (armorStand.getItemInHand() != null && armorStand.getItemInHand().getType() != Material.AIR) {
				entityData.setHand(ItemStackUtil.serializeItemStack(armorStand.getItemInHand()));
			}

			if (armorStand.getHelmet() != null && armorStand.getHelmet().getType() != Material.AIR) {
				entityData.setHelmet(ItemStackUtil.serializeItemStack(armorStand.getHelmet()));
			}

			if (armorStand.getChestplate() != null && armorStand.getChestplate().getType() != Material.AIR) {
				entityData.setChestplate(ItemStackUtil.serializeItemStack(armorStand.getChestplate()));
			}

			if (armorStand.getLeggings() != null && armorStand.getLeggings().getType() != Material.AIR) {
				entityData.setLeggings(ItemStackUtil.serializeItemStack(armorStand.getLeggings()));
			}

			if (armorStand.getBoots() != null && armorStand.getBoots().getType() != Material.AIR) {
				entityData.setBoots(ItemStackUtil.serializeItemStack(armorStand.getBoots()));
			}

			entityData.setBasePlate(armorStand.hasBasePlate());
			entityData.setVisible(armorStand.isVisible());
			entityData.setSmall(armorStand.isSmall());
			entityData.setMarker(armorStand.isMarker());
			entityData.setBodyPose(armorStand.getBodyPose().getX() + " " + armorStand.getBodyPose().getY() + " "
					+ armorStand.getBodyPose().getZ());
			entityData.setHeadPose(armorStand.getHeadPose().getX() + " " + armorStand.getHeadPose().getY() + " "
					+ armorStand.getHeadPose().getZ());
			entityData.setLeftArmPose(armorStand.getLeftArmPose().getX() + " " + armorStand.getLeftArmPose().getY()
					+ " " + armorStand.getLeftArmPose().getZ());
			entityData.setLeftLegPose(armorStand.getLeftLegPose().getX() + " " + armorStand.getLeftLegPose().getY()
					+ " " + armorStand.getLeftLegPose().getZ());
			entityData.setRightArmPose(armorStand.getRightArmPose().getX() + " " + armorStand.getRightArmPose().getY()
					+ " " + armorStand.getRightArmPose().getZ());
			entityData.setRightLegPose(armorStand.getRightLegPose().getX() + " " + armorStand.getRightLegPose().getY()
					+ " " + armorStand.getRightLegPose().getZ());

			return entityData;
		}

		int NMSVersion = NMSUtil.getVersionNumber();

		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			EntityEquipment entityEquipment = livingEntity.getEquipment();

			if (NMSVersion > 8) {
				if (NMSVersion > 9) {
					entityData.setAI(livingEntity.hasAI());
				}

				if (entityEquipment.getItemInMainHand() != null
						&& entityEquipment.getItemInMainHand().getType() != Material.AIR) {
					entityData.setHand(ItemStackUtil.serializeItemStack(entityEquipment.getItemInMainHand()));
				}

				entityData.setHandChance(entityEquipment.getItemInMainHandDropChance());

				if (entityEquipment.getItemInOffHand() != null
						&& entityEquipment.getItemInOffHand().getType() != Material.AIR) {
					entityData.setOffHand(ItemStackUtil.serializeItemStack(entityEquipment.getItemInOffHand()));
				}

				entityData.setOffHandChange(entityEquipment.getItemInOffHandDropChance());
			} else {
				if (entityEquipment.getItemInHand() != null
						&& entityEquipment.getItemInHand().getType() != Material.AIR) {
					entityData.setHand(ItemStackUtil.serializeItemStack(entityEquipment.getItemInHand()));
				}

				entityData.setHandChance(entityEquipment.getItemInHandDropChance());
			}

			if (entityEquipment.getHelmet() != null && entityEquipment.getHelmet().getType() != Material.AIR) {
				entityData.setHelmet(ItemStackUtil.serializeItemStack(entityEquipment.getHelmet()));
			}

			if (entityEquipment.getChestplate() != null && entityEquipment.getChestplate().getType() != Material.AIR) {
				entityData.setChestplate(ItemStackUtil.serializeItemStack(entityEquipment.getChestplate()));
			}

			if (entityEquipment.getLeggings() != null && entityEquipment.getLeggings().getType() != Material.AIR) {
				entityData.setLeggings(ItemStackUtil.serializeItemStack(entityEquipment.getLeggings()));
			}

			if (entityEquipment.getBoots() != null && entityEquipment.getBoots().getType() != Material.AIR) {
				entityData.setBoots(ItemStackUtil.serializeItemStack(entityEquipment.getBoots()));
			}

			entityData.setHelmetChance(entityEquipment.getHelmetDropChance());
			entityData.setChestplateChance(entityEquipment.getChestplateDropChance());
			entityData.setLeggingsChance(entityEquipment.getLeggingsDropChance());
			entityData.setBootsChance(entityEquipment.getBootsDropChance());

			if (entity instanceof Bat) {
				entityData.setAwake(((Bat) entityData).isAwake());
			} else if (entity instanceof Creeper) {
				entityData.setPowered(((Creeper) entity).isPowered());
			} else if (entity instanceof Enderman) {
				Enderman enderman = ((Enderman) entity);

				if (NMSVersion > 12) {
					if (enderman.getCarriedBlock() == null) {
						entityData.setCarryBlock("");
					} else {
						entityData.setCarryBlock(enderman.getCarriedBlock().getMaterial().name() + ":0");
					}
				} else {
					MaterialData materialData = enderman.getCarriedMaterial();

					if (materialData == null) {
						entityData.setCarryBlock("");
					} else {
						entityData.setCarryBlock(materialData.getItemType().toString() + ":" + materialData.getData());
					}
				}
			} else if (entity instanceof Horse) {
				Horse horse = ((Horse) entity);
				entityData.setHorseColor(horse.getColor().toString());
				entityData.setHorseStyle(horse.getStyle().toString());

				List<String> items = new ArrayList<>();

				for (ItemStack itemList : horse.getInventory().getContents()) {
					if (itemList != null && itemList.getType() != Material.AIR) {
						items.add(ItemStackUtil.serializeItemStack(itemList));
					}
				}

				entityData.setInventory(items.toArray(new String[0]));
			} else if (entity instanceof IronGolem) {
				entityData.setCreatedByPlayer(((IronGolem) entity).isPlayerCreated());
			} else if (entity instanceof Ocelot) {
				entityData.setOcelotType(((Ocelot) entity).getCatType().toString());
			} else if (entity instanceof Pig) {
				entityData.setSaddle(((Pig) entity).hasSaddle());
			} else if (entity instanceof Zombie) {
				entityData.setBaby(((Zombie) entity).isBaby());
			} else if (entity instanceof PigZombie) {
				PigZombie pigZombie = ((PigZombie) entity);
				entityData.setAngry(pigZombie.isAngry());
				entityData.setAngerLevel(pigZombie.getAnger());
			} else if (entity instanceof Rabbit) {
				entityData.setRabbitType(((Rabbit) entity).getRabbitType().toString());
			} else if (entity instanceof Sheep) {
				entityData.setSheared(((Sheep) entity).isSheared());
				entityData.setColor(((Colorable) entity).getColor().toString());
			} else if (entity instanceof Slime) {
				entityData.setSlimeSize(((Slime) entity).getSize());
			} else if (entity instanceof Snowman) {
				entityData.setDerp(((Snowman) entity).isDerp());
			} else if (entity instanceof Villager) {
				Villager villager = ((Villager) entity);
				entityData.setProfession(villager.getProfession().toString());
				entityData.setRiches(villager.getRiches());

				List<String> items = new ArrayList<>();

				for (ItemStack itemList : villager.getInventory().getContents()) {
					if (itemList != null && itemList.getType() != Material.AIR) {
						items.add(ItemStackUtil.serializeItemStack(itemList));
					}
				}

				entityData.setInventory(items.toArray(new String[0]));
			}

			if (NMSVersion > 10) {
				if (entity instanceof Llama) {
					Llama llama = ((Llama) entity);
					entityData.setLlamaColor(llama.getColor().toString());
					entityData.setLlamaStrength(llama.getStrength());

					List<String> items = new ArrayList<>();

					for (ItemStack itemList : llama.getInventory().getContents()) {
						if (itemList != null && itemList.getType() != Material.AIR) {
							items.add(ItemStackUtil.serializeItemStack(itemList));
						}
					}
				}

				if (NMSVersion > 11) {
					if (entity instanceof Parrot) {
						entityData.setParrotVariant(((Parrot) entity).getVariant().toString());
					}
				}
			}
		}

		if (entity instanceof Ageable) {
			Ageable ageable = ((Ageable) entity);
			entityData.setBreed(ageable.canBreed());
			entityData.setAge(ageable.getAge());
			entityData.setAgeLock(ageable.getAgeLock());
			entityData.setBaby(!ageable.isAdult());
		} else if (entity instanceof Vehicle) {
			if (entity instanceof Boat) {
				entityData.setWoodType(((Boat) entity).getWoodType().toString());
			} else if (entity instanceof StorageMinecart || entity instanceof HopperMinecart) {
				List<String> items = new ArrayList<>();

				for (ItemStack itemList : ((InventoryHolder) entity).getInventory().getContents()) {
					if (itemList != null && itemList.getType() != Material.AIR) {
						items.add(ItemStackUtil.serializeItemStack(itemList));
					}
				}

				entityData.setInventory(items.toArray(new String[0]));
			}
		} else if (entity instanceof Hanging) {
			if (entity instanceof ItemFrame) {
				ItemFrame itemFrame = ((ItemFrame) entity);
				ItemStack is = itemFrame.getItem();

				if (is == null) {
					entityData.setItem("");
				} else {
					entityData.setItem(ItemStackUtil.serializeItemStack(is));
				}

				entityData.setRotate(itemFrame.getRotation().toString());
			} else if (entity instanceof Painting) {
				entityData.setArt(((Painting) entity).getArt().toString());
			}
		}

		return entityData;
	}

	public static void convertEntityDataToEntity(EntityData entityData, Location loc, BlockDegreesType type) {
		Entity entity = loc.getWorld().spawnEntity(loc, EntityType.valueOf(entityData.getEntityType().toUpperCase()));
		entity.setCustomName(entityData.getCustomName());
		entity.setCustomNameVisible(entityData.isCustomNameVisible());
		entity.setFireTicks(entityData.getFireTicks());
		entity.setTicksLived(entityData.getTicksLived());

		if (entity instanceof ArmorStand) {
			ArmorStand armorStand = (ArmorStand) entity;
			armorStand.setArms(entityData.hasArms());

			if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
				armorStand.setItemInHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
			}

			if (entityData.getHelmet() != null && !entityData.getHelmet().isEmpty()) {
				armorStand.setHelmet(ItemStackUtil.deserializeItemStack(entityData.getHelmet()));
			}

			if (entityData.getChestplate() != null && !entityData.getChestplate().isEmpty()) {
				armorStand.setChestplate(ItemStackUtil.deserializeItemStack(entityData.getChestplate()));
			}

			if (entityData.getLeggings() != null && !entityData.getLeggings().isEmpty()) {
				armorStand.setLeggings(ItemStackUtil.deserializeItemStack(entityData.getLeggings()));
			}

			if (entityData.getBoots() != null && !entityData.getBoots().isEmpty()) {
				armorStand.setBoots(ItemStackUtil.deserializeItemStack(entityData.getBoots()));
			}

			armorStand.setBasePlate(entityData.hasBasePlate());
			armorStand.setVisible(entityData.isVisible());
			armorStand.setSmall(entityData.isSmall());
			armorStand.setMarker(entityData.isMarker());

			String[] bodyPose = entityData.getBodyPose().split(" ");
			armorStand.setBodyPose(new EulerAngle(Double.parseDouble(bodyPose[0]), Double.parseDouble(bodyPose[1]),
					Double.parseDouble(bodyPose[2])));

			String[] headPose = entityData.getHeadPose().split(" ");
			armorStand.setHeadPose(new EulerAngle(Double.parseDouble(headPose[0]), Double.parseDouble(headPose[1]),
					Double.parseDouble(headPose[2])));

			String[] leftArmPose = entityData.getLeftArmPose().split(" ");
			armorStand.setLeftArmPose(new EulerAngle(Double.parseDouble(leftArmPose[0]),
					Double.parseDouble(leftArmPose[1]), Double.parseDouble(leftArmPose[2])));

			String[] leftLegPose = entityData.getLeftLegPose().split(" ");
			armorStand.setLeftLegPose(new EulerAngle(Double.parseDouble(leftLegPose[0]),
					Double.parseDouble(leftLegPose[1]), Double.parseDouble(leftLegPose[2])));

			String[] rightArmPose = entityData.getRightArmPose().split(" ");
			armorStand.setRightArmPose(new EulerAngle(Double.parseDouble(rightArmPose[0]),
					Double.parseDouble(rightArmPose[1]), Double.parseDouble(rightArmPose[2])));

			String[] rightLegPose = entityData.getRightLegPose().split(" ");
			armorStand.setRightLegPose(new EulerAngle(Double.parseDouble(rightLegPose[0]),
					Double.parseDouble(rightLegPose[1]), Double.parseDouble(rightLegPose[2])));
		}

		int NMSVersion = NMSUtil.getVersionNumber();

		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			EntityEquipment entityEquipment = livingEntity.getEquipment();

			if (NMSVersion > 8) {
				if (NMSVersion > 9) {
					livingEntity.setAI(entityData.hasAI());
				}

				if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
					entityEquipment.setItemInMainHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
				}

				if (entityData.getOffHand() != null && !entityData.getOffHand().isEmpty()) {
					entityEquipment.setItemInOffHand(ItemStackUtil.deserializeItemStack(entityData.getOffHand()));
				}

				entityEquipment.setItemInMainHandDropChance(entityData.getHandChance());
				entityEquipment.setItemInOffHandDropChance(entityData.getOffHandChance());
			} else {
				if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
					entityEquipment.setItemInHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
				}

				entityEquipment.setItemInHandDropChance(entityData.getHandChance());
			}

			if (entityData.getHelmet() != null && !entityData.getHelmet().isEmpty()) {
				entityEquipment.setHelmet(ItemStackUtil.deserializeItemStack(entityData.getHelmet()));
			}

			if (entityData.getChestplate() != null && !entityData.getChestplate().isEmpty()) {
				entityEquipment.setChestplate(ItemStackUtil.deserializeItemStack(entityData.getChestplate()));
			}

			if (entityData.getLeggings() != null && !entityData.getLeggings().isEmpty()) {
				entityEquipment.setLeggings(ItemStackUtil.deserializeItemStack(entityData.getLeggings()));
			}

			if (entityData.getBoots() != null && !entityData.getBoots().isEmpty()) {
				entityEquipment.setBoots(ItemStackUtil.deserializeItemStack(entityData.getBoots()));
			}

			entityEquipment.setHelmetDropChance(entityData.getHelmetChance());
			entityEquipment.setChestplateDropChance(entityData.getChestplateChance());
			entityEquipment.setLeggingsDropChance(entityData.getLeggingsChance());
			entityEquipment.setBootsDropChance(entityData.getBootsChance());

			if (entity instanceof Bat) {
				((Bat) entity).setAwake(entityData.isAwake());
			} else if (entity instanceof Creeper) {
				((Creeper) entity).setPowered(entityData.isPowered());
			} else if (entity instanceof Enderman) {
				if (entityData.getCarryBlock() != null && !entityData.getCarryBlock().isEmpty()) {
					String[] materialData = entityData.getCarryBlock().split(":");

					byte data = Byte.parseByte(materialData[1]);
					Material material = MaterialUtil.getMaterial(NMSVersion, entityData.getVersion(),
							materialData[0].toUpperCase(), data);

					if (material != null) {
						if (NMSVersion > 12) {
							((Enderman) entity).setCarriedBlock(Bukkit.getServer().createBlockData(material));
						} else {
							((Enderman) entity).setCarriedMaterial(new MaterialData(material, data));
						}
					}
				}
			} else if (entity instanceof Horse) {
				Horse horse = ((Horse) entity);
				horse.setColor(Horse.Color.valueOf(entityData.getHorseColor().toUpperCase()));
				horse.setStyle(Horse.Style.valueOf(entityData.getHorseStyle().toUpperCase()));

				List<ItemStack> items = new ArrayList<>();

				for (String inventoryList : entityData.getInventory()) {
					items.add(ItemStackUtil.deserializeItemStack(inventoryList));
				}

				horse.getInventory().setContents(items.toArray(new ItemStack[0]));
			} else if (entity instanceof IronGolem) {
				((IronGolem) entity).setPlayerCreated(entityData.isCreatedByPlayer());
			} else if (entity instanceof Ocelot) {
				((Ocelot) entity).setCatType(Ocelot.Type.valueOf(entityData.getOcelotType().toUpperCase()));
			} else if (entity instanceof Pig) {
				((Pig) entity).setSaddle(entityData.hasSaddle());
			} else if (entity instanceof Zombie) {
				((Zombie) entity).setBaby(entityData.isBaby());
			} else if (entity instanceof PigZombie) {
				PigZombie pigZombie = ((PigZombie) entity);
				pigZombie.setAngry(entityData.isAngry());
				pigZombie.setAnger(entityData.getAngerLevel());
			} else if (entity instanceof Rabbit) {
				((Rabbit) entity).setRabbitType(Rabbit.Type.valueOf(entityData.getRabbitType().toUpperCase()));
			} else if (entity instanceof Sheep) {
				Sheep sheep = ((Sheep) entity);
				sheep.setSheared(entityData.isSheared());
				sheep.setColor(DyeColor.valueOf(entityData.getColor().toUpperCase()));
			} else if (entity instanceof Slime) {
				((Slime) entity).setSize(entityData.getSlimeSize());
			} else if (entity instanceof Snowman) {
				((Snowman) entity).setDerp(entityData.isDerp());
			} else if (entity instanceof Villager) {
				Villager villager = ((Villager) entity);
				villager.setProfession(Villager.Profession.valueOf(entityData.getProfession().toUpperCase()));

				List<ItemStack> items = new ArrayList<>();

				for (String inventoryList : entityData.getInventory()) {
					items.add(ItemStackUtil.deserializeItemStack(inventoryList));
				}

				villager.getInventory().setContents(items.toArray(new ItemStack[0]));
				villager.setRiches(entityData.getRiches());
			}

			if (NMSVersion > 10) {
				if (entity instanceof Llama) {
					Llama llama = ((Llama) entity);
					llama.setColor(Llama.Color.valueOf(entityData.getLlamaColor().toUpperCase()));
					llama.setStrength(entityData.getLlamaStrength());

					List<ItemStack> items = new ArrayList<>();

					for (String inventoryList : entityData.getInventory()) {
						items.add(ItemStackUtil.deserializeItemStack(inventoryList));
					}

					llama.getInventory().setContents(items.toArray(new ItemStack[0]));
				}

				if (NMSVersion > 11) {
					if (entity instanceof Parrot) {
						((Parrot) entity)
								.setVariant(Parrot.Variant.valueOf(entityData.getParrotVariant().toUpperCase()));
					}
				}
			}
		}

		if (entity instanceof Ageable) {
			Ageable ageable = ((Ageable) entity);
			ageable.setBreed(entityData.canBreed());
			ageable.setAge(entityData.getAge());
			ageable.setAgeLock(entityData.isAgeLock());

			if (!entityData.isBaby()) {
				ageable.setAdult();
			}
		} else if (entity instanceof Vehicle) {
			if (entity instanceof Boat) {
				((Boat) entity).setWoodType(TreeSpecies.valueOf(entityData.getWoodType().toUpperCase()));
			} else if (entity instanceof StorageMinecart || entity instanceof HopperMinecart) {

				List<ItemStack> items = new ArrayList<>();

				for (String inventoryList : entityData.getInventory()) {
					items.add(ItemStackUtil.deserializeItemStack(inventoryList));
				}

				((InventoryHolder) entity).getInventory().setContents(items.toArray(new ItemStack[0]));
			}
		} else if (entity instanceof Hanging) {
			if (entity instanceof ItemFrame) {
				ItemFrame itemFrame = ((ItemFrame) entity);

				if (0 < entityData.getItem().length()) {
					itemFrame.setItem(ItemStackUtil.deserializeItemStack(entityData.getItem()));
				}

				itemFrame.setRotation(Rotation.valueOf(entityData.getRotate().toUpperCase()));
			} else if (entity instanceof Painting) {
				((Painting) entity).setArt(Art.valueOf(entityData.getArt()));
			}
		}
	}
}
