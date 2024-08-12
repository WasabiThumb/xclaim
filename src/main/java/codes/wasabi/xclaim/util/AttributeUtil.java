package codes.wasabi.xclaim.util;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public final class AttributeUtil {

    public static double scaleDamage(
            double damage,
            @Nullable ItemStack boots,
            @Nullable ItemStack leggings,
            @Nullable ItemStack chestplate,
            @Nullable ItemStack helmet
    ) {
        damage = scaleDamage(damage, boots, EquipmentSlot.FEET, 3);
        damage = scaleDamage(damage, leggings, EquipmentSlot.LEGS, 2);
        damage = scaleDamage(damage, chestplate, EquipmentSlot.CHEST, 1);
        damage = scaleDamage(damage, helmet, EquipmentSlot.HEAD, 0);
        return damage;
    }

    private static final double[][] ARMOR_TABLE = new double[][] {
            new double[] { 1d, 3d, 2d, 1d }, // LEATHER
            new double[] { 2d, 5d, 3d, 1d }, // GOLDEN
            new double[] { 2d, 5d, 4d, 1d }, // CHAINMAIL
            new double[] { 2d, 6d, 5d, 2d }, // IRON
            new double[] { 3d, 8d, 6d, 3d }, // DIAMOND & NETHERITE
    };

    private static double scaleDamage(double damage, @Nullable ItemStack item, @NotNull EquipmentSlot slot, int slotCode) {
        if (item == null) return damage;
        if (item.getAmount() < 1) return damage;
        Material type = item.getType();
        if (!type.isItem()) return damage;

        String armorType = type.name().toUpperCase(Locale.ROOT);
        int temp = armorType.indexOf('_');
        if (temp != -1) armorType = armorType.substring(0, temp);

        double armor = 0d;
        double toughness = 0d;
        switch (armorType) {
            case "TURTLE":
                armor = 2d;
                break;
            case "LEATHER":
                armor = ARMOR_TABLE[0][slotCode];
                break;
            case "GOLDEN":
                armor = ARMOR_TABLE[1][slotCode];
                break;
            case "CHAINMAIL":
                armor = ARMOR_TABLE[2][slotCode];
                break;
            case "IRON":
                armor = ARMOR_TABLE[3][slotCode];
                break;
            case "DIAMOND":
                armor = ARMOR_TABLE[4][slotCode];
                toughness = 2d;
                break;
            case "NETHERITE":
                armor = ARMOR_TABLE[4][slotCode];
                toughness = 3d;
                break;
        }
        armor = scaleByAttributes(armor, type, slot, Attribute.GENERIC_ARMOR);
        toughness = scaleByAttributes(toughness, type, slot, Attribute.GENERIC_ARMOR_TOUGHNESS);

        double modification = armor - ((4d * damage) / (toughness + 8d));
        modification = Math.min(20d, Math.max(armor / 5d, modification));
        modification = 1d - (modification / 25d);
        damage *= modification;

        // Enchants (Protection)
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        Integer protectionLevel = enchants.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (protectionLevel != null && protectionLevel > 0) {
            damage *= (1d - (protectionLevel * 0.04d));
        }

        return damage;
    }

    private static double scaleByAttributes(
            double value,
            @NotNull Material type,
            @NotNull EquipmentSlot slot,
            @NotNull Attribute attribute
    ) {
        Multimap<Attribute, AttributeModifier> allModifiers = type.getDefaultAttributeModifiers(slot);
        if (allModifiers.isEmpty()) return value;
        Collection<AttributeModifier> modifier = allModifiers.get(attribute);
        if (modifier.isEmpty()) return value;

        for (AttributeModifier am : modifier) {
            switch (am.getOperation()) {
                case ADD_NUMBER:
                    value += am.getAmount();
                    break;
                case ADD_SCALAR:
                    value += (am.getAmount() * value);
                    break;
                case MULTIPLY_SCALAR_1:
                    value *= (am.getAmount() + 1);
                    break;
            }
        }

        return value;
    }

}
