package me.goodandevil.skyblock.utils.world.entity;

public class EntityData {

	private String entityType;
	private String hand;
	private String helmet;
	private String chestplate;
	private String leggings;
	private String boots;
	private String bodyPose;
	private String headPose;
	private String leftArmPose;
	private String leftLegPose;
	private String rightArmPose;
	private String rightLegPose;
	private String offHand;
	private String woodType;
	private String carryBlock;
	private String customName;
	private String horseColor;
	private String horseStyle;
	private String item;
	private String rotate;
	private String llamaColor;
	private String ocelotType;
	private String art;
	private String parrotVariant;
	private String rabbitType;
	private String profession;
	private String color;

	private String[] inventory;

	private double x;
	private double y;
	private double z;

	private float handChance;
	private float offHandChance;
	private float helmetChance;
	private float chestplateChance;
	private float leggingsChance;
	private float bootsChance;
	private float yaw = 0;
	private float pitch = 0;

	private int version;
	private int fireTicks;
	private int ticksLived;
	private int llamaStrength;
	private int angerLevel;
	private int slimeSize;
	private int riches;
	private int age;

	private boolean arms;
	private boolean basePlate;
	private boolean visible;
	private boolean small;
	private boolean marker;
	private boolean awake;
	private boolean powered;
	private boolean customNameVisible;
	private boolean createdByPlayer;
	private boolean saddle;
	private boolean angry;
	private boolean sheared;
	private boolean derp;
	private boolean ageLock;
	private boolean breed;
	private boolean ai;
	private boolean baby;

	public EntityData(String entityType, double x, double y, double z, String customName, boolean customNameVisible,
			int fireTicks, int ticksLived) {
		this.entityType = entityType;

		this.x = x;
		this.y = y;
		this.z = z;

		this.customName = customName;
		this.customNameVisible = customNameVisible;

		this.fireTicks = fireTicks;
		this.ticksLived = ticksLived;

		this.ai = true;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getHand() {
		return this.hand;
	}

	public void setHand(String hand) {
		this.hand = hand;
	}

	public String getHelmet() {
		return this.helmet;
	}

	public void setHelmet(String helmet) {
		this.helmet = helmet;
	}

	public String getChestplate() {
		return this.chestplate;
	}

	public void setChestplate(String chestplate) {
		this.chestplate = chestplate;
	}

	public String getLeggings() {
		return this.leggings;
	}

	public void setLeggings(String leggings) {
		this.leggings = leggings;
	}

	public String getBoots() {
		return this.boots;
	}

	public void setBoots(String boots) {
		this.boots = boots;
	}

	public String getBodyPose() {
		return this.bodyPose;
	}

	public void setBodyPose(String bodyPose) {
		this.bodyPose = bodyPose;
	}

	public String getHeadPose() {
		return this.headPose;
	}

	public void setHeadPose(String headPose) {
		this.headPose = headPose;
	}

	public String getLeftArmPose() {
		return this.leftArmPose;
	}

	public void setLeftArmPose(String leftArmPose) {
		this.leftArmPose = leftArmPose;
	}

	public String getLeftLegPose() {
		return this.leftLegPose;
	}

	public void setLeftLegPose(String leftLegPose) {
		this.leftLegPose = leftLegPose;
	}

	public String getRightArmPose() {
		return this.rightArmPose;
	}

	public void setRightArmPose(String rightArmPose) {
		this.rightArmPose = rightArmPose;
	}

	public String getRightLegPose() {
		return this.rightLegPose;
	}

	public void setRightLegPose(String rightLegPose) {
		this.rightLegPose = rightLegPose;
	}

	public String getOffHand() {
		return this.offHand;
	}

	public void setOffHand(String offHand) {
		this.offHand = offHand;
	}

	public String getWoodType() {
		return this.woodType;
	}

	public void setWoodType(String woodType) {
		this.woodType = woodType;
	}

	public String getCarryBlock() {
		return this.carryBlock;
	}

	public void setCarryBlock(String carryBlock) {
		this.carryBlock = carryBlock;
	}

	public String getCustomName() {
		return this.customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getHorseColor() {
		return this.horseColor;
	}

	public void setHorseColor(String horseColor) {
		this.horseColor = horseColor;
	}

	public String getHorseStyle() {
		return this.horseStyle;
	}

	public void setHorseStyle(String horseStyle) {
		this.horseStyle = horseStyle;
	}

	public String getItem() {
		return this.item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getRotate() {
		return this.rotate;
	}

	public void setRotate(String rotate) {
		this.rotate = rotate;
	}

	public String getLlamaColor() {
		return this.llamaColor;
	}

	public void setLlamaColor(String llamaColor) {
		this.llamaColor = llamaColor;
	}

	public String getOcelotType() {
		return this.ocelotType;
	}

	public void setOcelotType(String ocelotType) {
		this.ocelotType = ocelotType;
	}

	public String getArt() {
		return this.art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getParrotVariant() {
		return this.parrotVariant;
	}

	public void setParrotVariant(String parrotVariant) {
		this.parrotVariant = parrotVariant;
	}

	public String getRabbitType() {
		return this.rabbitType;
	}

	public void setRabbitType(String rabbitType) {
		this.rabbitType = rabbitType;
	}

	public String getProfession() {
		return this.profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String[] getInventory() {
		return this.inventory;
	}

	public void setInventory(String[] inventory) {
		this.inventory = inventory;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getHandChance() {
		return this.handChance;
	}

	public void setHandChance(float handChance) {
		this.handChance = handChance;
	}

	public float getOffHandChance() {
		return this.offHandChance;
	}

	public void setOffHandChange(float offHandChange) {
		this.offHandChance = offHandChange;
	}

	public float getHelmetChance() {
		return this.helmetChance;
	}

	public void setHelmetChance(float helmetChance) {
		this.helmetChance = helmetChance;
	}

	public float getChestplateChance() {
		return this.chestplateChance;
	}

	public void setChestplateChance(float chestplateChance) {
		this.chestplateChance = chestplateChance;
	}

	public float getLeggingsChance() {
		return this.leggingsChance;
	}

	public void setLeggingsChance(float leggingsChance) {
		this.leggingsChance = leggingsChance;
	}

	public float getBootsChance() {
		return this.bootsChance;
	}

	public void setBootsChance(float bootsChance) {
		this.bootsChance = bootsChance;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getFireTicks() {
		return this.fireTicks;
	}

	public void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}

	public int getTicksLived() {
		return this.ticksLived;
	}

	public void setTicksLived(int ticksLived) {
		this.ticksLived = ticksLived;
	}

	public int getLlamaStrength() {
		return this.llamaStrength;
	}

	public void setLlamaStrength(int llamaStrength) {
		this.llamaStrength = llamaStrength;
	}

	public int getAngerLevel() {
		return this.angerLevel;
	}

	public void setAngerLevel(int angerLevel) {
		this.angerLevel = angerLevel;
	}

	public int getSlimeSize() {
		return this.slimeSize;
	}

	public void setSlimeSize(int slimeSize) {
		this.slimeSize = slimeSize;
	}

	public int getRiches() {
		return this.riches;
	}

	public void setRiches(int riches) {
		this.riches = riches;
	}

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean hasArms() {
		return this.arms;
	}

	public void setArms(boolean arms) {
		this.arms = arms;
	}

	public boolean hasBasePlate() {
		return this.basePlate;
	}

	public void setBasePlate(boolean basePlate) {
		this.basePlate = basePlate;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSmall() {
		return this.small;
	}

	public void setSmall(boolean small) {
		this.small = small;
	}

	public boolean isMarker() {
		return this.marker;
	}

	public void setMarker(boolean marker) {
		this.marker = marker;
	}

	public boolean isAwake() {
		return this.awake;
	}

	public void setAwake(boolean awake) {
		this.awake = awake;
	}

	public boolean isPowered() {
		return this.powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	public boolean isCustomNameVisible() {
		return this.customNameVisible;
	}

	public void setCustomNameVisible(boolean customNameVisible) {
		this.customNameVisible = customNameVisible;
	}

	public boolean isCreatedByPlayer() {
		return this.createdByPlayer;
	}

	public void setCreatedByPlayer(boolean createdByPlayer) {
		this.createdByPlayer = createdByPlayer;
	}

	public boolean hasSaddle() {
		return this.saddle;
	}

	public void setSaddle(boolean saddle) {
		this.saddle = saddle;
	}

	public boolean isAngry() {
		return this.angry;
	}

	public void setAngry(boolean angry) {
		this.angry = angry;
	}

	public boolean isSheared() {
		return this.sheared;
	}

	public void setSheared(boolean sheared) {
		this.sheared = sheared;
	}

	public boolean isDerp() {
		return this.derp;
	}

	public void setDerp(boolean derp) {
		this.derp = derp;
	}

	public boolean isAgeLock() {
		return this.ageLock;
	}

	public void setAgeLock(boolean ageLock) {
		this.ageLock = ageLock;
	}

	public boolean canBreed() {
		return this.breed;
	}

	public void setBreed(boolean breed) {
		this.breed = breed;
	}

	public boolean hasAI() {
		return this.ai;
	}

	public void setAI(boolean ai) {
		this.ai = ai;
	}

	public boolean isBaby() {
		return this.baby;
	}

	public void setBaby(boolean baby) {
		this.baby = baby;
	}
}
