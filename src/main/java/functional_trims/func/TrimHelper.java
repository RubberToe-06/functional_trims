package functional_trims.func;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class TrimHelper {
    public static int countTrim(Player player, ResourceKey<TrimMaterial> materialKey) {
        int trimCount = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET}) {

            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                ArmorTrim trim = stack.get(DataComponents.TRIM);
                if (trim != null && trim.material().is(materialKey)) {
                    trimCount++;
                }
            }
        }

        return trimCount;
    }

    public static boolean hasFullTrim(Player player, ResourceKey<TrimMaterial> materialKey) {
        int trimCount = countTrim(player, materialKey);
        return trimCount == 4;
    }
}