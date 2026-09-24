package com.frontline.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** Бинокль: подсвечивает враждебных мобов в радиусе 48 блоков на 10 секунд. */
public class BinocularsItem extends Item {
    public BinocularsItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            AABB box = player.getBoundingBox().inflate(48);
            List<Monster> targets = level.getEntitiesOfClass(Monster.class, box);
            for (Monster m : targets) {
                m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
            }
            player.getCooldowns().addCooldown(this, 200);
            player.displayClientMessage(Component.translatable("msg.frontline.scan", targets.size()), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
