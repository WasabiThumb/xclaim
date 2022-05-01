package codes.wasabi.xclaim.command.argument.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class StandardTypes {

    public static final Type<String> STRING = new StringType();
    public static final Type<Integer> INTEGER = new IntType();
    public static final Type<Float> FLOAT = new FloatType();
    public static final Type<Player> PLAYER = new PlayerType();
    public static final Type<Material> MATERIAL = new MaterialType();
    public static final Type<Material> BLOCK_MATERIAL = new BlockMaterialType();
    public static final Type<Material> ITEM_MATERIAL = new ItemMaterialType();

}
