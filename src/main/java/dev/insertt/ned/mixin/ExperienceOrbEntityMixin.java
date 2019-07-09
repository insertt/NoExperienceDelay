package dev.insertt.ned.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map.Entry;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity {

    @Shadow
    private int amount;

    public ExperienceOrbEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Overwrite
    public void onPlayerCollision(PlayerEntity player) {
        if (! this.world.isClient) {
            player.sendPickup(this, 1);
            Entry<EquipmentSlot, ItemStack> entries = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.MENDING, player);
            if (entries != null) {
                ItemStack itemStack = entries.getValue();
                if (! itemStack.isEmpty() && itemStack.isDamaged()) {
                    int int_1 = Math.min(this.getMendingRepairAmount(this.amount), itemStack.getDamage());
                    this.amount -= this.getMendingRepairCost(int_1);
                    itemStack.setDamage(itemStack.getDamage() - int_1);
                }
            }

            if (this.amount > 0) {
                player.addExperience(this.amount);
            }

            this.remove();
        }
    }

    private int getMendingRepairCost(int cost) {
        return cost / 2;
    }

    private int getMendingRepairAmount(int amount) {
        return amount * 2;
    }
}
