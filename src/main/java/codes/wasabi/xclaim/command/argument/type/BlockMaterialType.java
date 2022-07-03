package codes.wasabi.xclaim.command.argument.type;

import codes.wasabi.xclaim.XClaim;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlockMaterialType extends MaterialType {

    @Override
    public @NotNull String getTypeName() {
        return XClaim.lang.get("arg-blockMaterial-name");
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        List<String> ret = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isBlock()) ret.add(mat.name());
        }
        return ret;
    }

    @Override
    protected boolean validate(@NotNull Material value) {
        return value.isBlock();
    }

}
