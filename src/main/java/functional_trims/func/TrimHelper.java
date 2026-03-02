package functional_trims.func;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;

public class TrimHelper {
    public static int countTrim(PlayerEntity player, RegistryKey<ArmorTrimMaterial> materialKey) {
        int trimCount = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET}) {

            ItemStack stack = player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ArmorTrim trim = stack.get(DataComponentTypes.TRIM);
                if (trim != null && trim.getMaterial().matchesKey(materialKey)) {
                    trimCount++;
                }
            }
        }

        return trimCount;
    }

    public static boolean hasFullTrim(PlayerEntity player, RegistryKey<ArmorTrimMaterial> materialKey) {
        int trimCount = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET}) {

            ItemStack stack = player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ArmorTrim trim = stack.get(DataComponentTypes.TRIM);
                if (trim != null && trim.getMaterial().matchesKey(materialKey)) {
                    trimCount++;
                }
            }
        }

        return trimCount == 4;
    }
}


