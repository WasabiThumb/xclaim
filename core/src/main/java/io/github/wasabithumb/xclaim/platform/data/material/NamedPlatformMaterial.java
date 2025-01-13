package io.github.wasabithumb.xclaim.platform.data.material;

public enum NamedPlatformMaterial implements PlatformMaterial {
    PLAYER_HEAD,
    GREEN_CONCRETE,
    RED_CONCRETE,
    SKELETON_SKULL,
    ENCHANTING_TABLE,
    CHEST_MINECART,
    CRAFTING_TABLE,
    FIREWORK_ROCKET,
    GREEN_DYE,
    RED_DYE,
    YELLOW_DYE,
    ORANGE_DYE,
    LIME_DYE,
    SHIELD,
    SPYGLASS,
    ARROW,
    BARRIER,
    NETHER_STAR,
    NAME_TAG,
    TNT,
    BUCKET,
    EMERALD,
    BOOK,
    WRITTEN_BOOK;

    @Override
    public boolean isSoil() {
        return false;
    }

    @Override
    public boolean ignites() {
        return false;
    }

    @Override
    public boolean isBucket() {
        return this == BUCKET;
    }

}
