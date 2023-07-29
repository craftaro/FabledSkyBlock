package com.songoda.skyblock.utils.world.entity;

public class EntityData {
    private byte[] serializedNBT;

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

    @Deprecated
    public EntityData(String entityType, double x, double y, double z, String customName, boolean customNameVisible, int fireTicks, int ticksLived) {
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

    public EntityData(byte[] serializedNBT, double x, double y, double z) {
        this.serializedNBT = serializedNBT;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public byte[] getSerializedNBT() {
        return serializedNBT;
    }

    public void setSerializedNBT(byte[] serializedNBT) {
        this.serializedNBT = serializedNBT;
    }

    @Deprecated
    public String getEntityType() {
        return entityType;
    }

    @Deprecated
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Deprecated
    public String getHand() {
        return this.hand;
    }

    @Deprecated
    public void setHand(String hand) {
        this.hand = hand;
    }

    @Deprecated
    public String getHelmet() {
        return this.helmet;
    }

    @Deprecated
    public void setHelmet(String helmet) {
        this.helmet = helmet;
    }

    @Deprecated
    public String getChestplate() {
        return this.chestplate;
    }

    @Deprecated
    public void setChestplate(String chestplate) {
        this.chestplate = chestplate;
    }

    @Deprecated
    public String getLeggings() {
        return this.leggings;
    }

    @Deprecated
    public void setLeggings(String leggings) {
        this.leggings = leggings;
    }

    @Deprecated
    public String getBoots() {
        return this.boots;
    }

    @Deprecated
    public void setBoots(String boots) {
        this.boots = boots;
    }

    @Deprecated
    public String getBodyPose() {
        return this.bodyPose;
    }

    @Deprecated
    public void setBodyPose(String bodyPose) {
        this.bodyPose = bodyPose;
    }

    @Deprecated
    public String getHeadPose() {
        return this.headPose;
    }

    @Deprecated
    public void setHeadPose(String headPose) {
        this.headPose = headPose;
    }

    @Deprecated
    public String getLeftArmPose() {
        return this.leftArmPose;
    }

    @Deprecated
    public void setLeftArmPose(String leftArmPose) {
        this.leftArmPose = leftArmPose;
    }

    @Deprecated
    public String getLeftLegPose() {
        return this.leftLegPose;
    }

    @Deprecated
    public void setLeftLegPose(String leftLegPose) {
        this.leftLegPose = leftLegPose;
    }

    @Deprecated
    public String getRightArmPose() {
        return this.rightArmPose;
    }

    @Deprecated
    public void setRightArmPose(String rightArmPose) {
        this.rightArmPose = rightArmPose;
    }

    @Deprecated
    public String getRightLegPose() {
        return this.rightLegPose;
    }

    @Deprecated
    public void setRightLegPose(String rightLegPose) {
        this.rightLegPose = rightLegPose;
    }

    @Deprecated
    public String getOffHand() {
        return this.offHand;
    }

    @Deprecated
    public void setOffHand(String offHand) {
        this.offHand = offHand;
    }

    @Deprecated
    public String getWoodType() {
        return this.woodType;
    }

    @Deprecated
    public void setWoodType(String woodType) {
        this.woodType = woodType;
    }

    @Deprecated
    public String getCarryBlock() {
        return this.carryBlock;
    }

    @Deprecated
    public void setCarryBlock(String carryBlock) {
        this.carryBlock = carryBlock;
    }

    @Deprecated
    public String getCustomName() {
        return this.customName;
    }

    @Deprecated
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Deprecated
    public String getHorseColor() {
        return this.horseColor;
    }

    @Deprecated
    public void setHorseColor(String horseColor) {
        this.horseColor = horseColor;
    }

    @Deprecated
    public String getHorseStyle() {
        return this.horseStyle;
    }

    @Deprecated
    public void setHorseStyle(String horseStyle) {
        this.horseStyle = horseStyle;
    }

    @Deprecated
    public String getItem() {
        return this.item;
    }

    @Deprecated
    public void setItem(String item) {
        this.item = item;
    }

    @Deprecated
    public String getRotate() {
        return this.rotate;
    }

    @Deprecated
    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

    @Deprecated
    public String getLlamaColor() {
        return this.llamaColor;
    }

    @Deprecated
    public void setLlamaColor(String llamaColor) {
        this.llamaColor = llamaColor;
    }

    @Deprecated
    public String getOcelotType() {
        return this.ocelotType;
    }

    @Deprecated
    public void setOcelotType(String ocelotType) {
        this.ocelotType = ocelotType;
    }

    @Deprecated
    public String getArt() {
        return this.art;
    }

    @Deprecated
    public void setArt(String art) {
        this.art = art;
    }

    @Deprecated
    public String getParrotVariant() {
        return this.parrotVariant;
    }

    @Deprecated
    public void setParrotVariant(String parrotVariant) {
        this.parrotVariant = parrotVariant;
    }

    @Deprecated
    public String getRabbitType() {
        return this.rabbitType;
    }

    @Deprecated
    public void setRabbitType(String rabbitType) {
        this.rabbitType = rabbitType;
    }

    @Deprecated
    public String getProfession() {
        return this.profession;
    }

    @Deprecated
    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Deprecated
    public String getColor() {
        return this.color;
    }

    @Deprecated
    public void setColor(String color) {
        this.color = color;
    }

    @Deprecated
    public String[] getInventory() {
        return this.inventory;
    }

    @Deprecated
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

    @Deprecated
    public float getHandChance() {
        return this.handChance;
    }

    @Deprecated
    public void setHandChance(float handChance) {
        this.handChance = handChance;
    }

    @Deprecated
    public float getOffHandChance() {
        return this.offHandChance;
    }

    @Deprecated
    public void setOffHandChange(float offHandChange) {
        this.offHandChance = offHandChange;
    }

    @Deprecated
    public float getHelmetChance() {
        return this.helmetChance;
    }

    @Deprecated
    public void setHelmetChance(float helmetChance) {
        this.helmetChance = helmetChance;
    }

    @Deprecated
    public float getChestplateChance() {
        return this.chestplateChance;
    }

    @Deprecated
    public void setChestplateChance(float chestplateChance) {
        this.chestplateChance = chestplateChance;
    }

    @Deprecated
    public float getLeggingsChance() {
        return this.leggingsChance;
    }

    @Deprecated
    public void setLeggingsChance(float leggingsChance) {
        this.leggingsChance = leggingsChance;
    }

    @Deprecated
    public float getBootsChance() {
        return this.bootsChance;
    }

    @Deprecated
    public void setBootsChance(float bootsChance) {
        this.bootsChance = bootsChance;
    }

    @Deprecated
    public float getYaw() {
        return this.yaw;
    }

    @Deprecated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Deprecated
    public float getPitch() {
        return this.pitch;
    }

    @Deprecated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Deprecated
    public int getVersion() {
        return this.version;
    }

    @Deprecated
    public void setVersion(int version) {
        this.version = version;
    }

    @Deprecated
    public int getFireTicks() {
        return this.fireTicks;
    }

    @Deprecated
    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Deprecated
    public int getTicksLived() {
        return this.ticksLived;
    }

    @Deprecated
    public void setTicksLived(int ticksLived) {
        this.ticksLived = ticksLived;
    }

    @Deprecated
    public int getLlamaStrength() {
        return this.llamaStrength;
    }

    @Deprecated
    public void setLlamaStrength(int llamaStrength) {
        this.llamaStrength = llamaStrength;
    }

    @Deprecated
    public int getAngerLevel() {
        return this.angerLevel;
    }

    @Deprecated
    public void setAngerLevel(int angerLevel) {
        this.angerLevel = angerLevel;
    }

    @Deprecated
    public int getSlimeSize() {
        return this.slimeSize;
    }

    @Deprecated
    public void setSlimeSize(int slimeSize) {
        this.slimeSize = slimeSize;
    }

    @Deprecated
    public int getAge() {
        return this.age;
    }

    @Deprecated
    public void setAge(int age) {
        this.age = age;
    }

    @Deprecated
    public boolean hasArms() {
        return this.arms;
    }

    @Deprecated
    public void setArms(boolean arms) {
        this.arms = arms;
    }

    @Deprecated
    public boolean hasBasePlate() {
        return this.basePlate;
    }

    @Deprecated
    public void setBasePlate(boolean basePlate) {
        this.basePlate = basePlate;
    }

    @Deprecated
    public boolean isVisible() {
        return this.visible;
    }

    @Deprecated
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Deprecated
    public boolean isSmall() {
        return this.small;
    }

    @Deprecated
    public void setSmall(boolean small) {
        this.small = small;
    }

    @Deprecated
    public boolean isMarker() {
        return this.marker;
    }

    @Deprecated
    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    @Deprecated
    public boolean isAwake() {
        return this.awake;
    }

    @Deprecated
    public void setAwake(boolean awake) {
        this.awake = awake;
    }

    @Deprecated
    public boolean isPowered() {
        return this.powered;
    }

    @Deprecated
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Deprecated
    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    @Deprecated
    public void setCustomNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
    }

    @Deprecated
    public boolean isCreatedByPlayer() {
        return this.createdByPlayer;
    }

    @Deprecated
    public void setCreatedByPlayer(boolean createdByPlayer) {
        this.createdByPlayer = createdByPlayer;
    }

    @Deprecated
    public boolean hasSaddle() {
        return this.saddle;
    }

    @Deprecated
    public void setSaddle(boolean saddle) {
        this.saddle = saddle;
    }

    @Deprecated
    public boolean isAngry() {
        return this.angry;
    }

    @Deprecated
    public void setAngry(boolean angry) {
        this.angry = angry;
    }

    @Deprecated
    public boolean isSheared() {
        return this.sheared;
    }

    @Deprecated
    public void setSheared(boolean sheared) {
        this.sheared = sheared;
    }

    @Deprecated
    public boolean isDerp() {
        return this.derp;
    }

    @Deprecated
    public void setDerp(boolean derp) {
        this.derp = derp;
    }

    @Deprecated
    public boolean isAgeLock() {
        return this.ageLock;
    }

    @Deprecated
    public void setAgeLock(boolean ageLock) {
        this.ageLock = ageLock;
    }

    @Deprecated
    public boolean canBreed() {
        return this.breed;
    }

    @Deprecated
    public void setBreed(boolean breed) {
        this.breed = breed;
    }

    @Deprecated
    public boolean hasAI() {
        return this.ai;
    }

    @Deprecated
    public void setAI(boolean ai) {
        this.ai = ai;
    }

    @Deprecated
    public boolean isBaby() {
        return this.baby;
    }

    @Deprecated
    public void setBaby(boolean baby) {
        this.baby = baby;
    }
}
