package functional_trims.func;

import functional_trims.FunctionalTrims;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
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
                if (trim != null && trim.material().matchesKey(materialKey)) {
                    trimCount++;
                }
            }
        }

        return trimCount;
    }
}


