package com.frontline.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Рация: отправляет твои координаты игрокам в радиусе 200 блоков. */
public class RadioItem extends Item {
    public RadioItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            BlockPos p = player.blockPosition();
            Component msg = Component.translatable("msg.frontline.radio",
                    player.getDisplayName(), p.getX(), p.getY(), p.getZ());
            for (ServerPlayer other : serverLevel.players()) {
                if (other.distanceToSqr(player) < 200.0 * 200.0) {
                    other.sendSystemMessage(msg);
                }
            }
            player.getCooldowns().addCooldown(this, 100);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
