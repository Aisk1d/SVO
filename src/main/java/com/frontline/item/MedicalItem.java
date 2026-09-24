package com.frontline.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Бинт / аптечка: лечит за время использования. */
public class MedicalItem extends Item {
    private final float heal;
    private final int useTicks;

    public MedicalItem(Properties props, float heal, int useTicks) {
        super(props);
        this.heal = heal;
        this.useTicks = useTicks;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return useTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getHealth() >= player.getMaxHealth()) {
            return InteractionResultHolder.fail(stack);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.heal(heal);
            if (entity instanceof Player player) {
                player.getCooldowns().addCooldown(this, 20);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }
        return stack;
    }
}
