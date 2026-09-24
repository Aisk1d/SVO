package com.frontline.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/** Целеуказатель: хранит координаты цели. ПКМ по блоку — цель на этот блок; /frontline target x y z — любые координаты. */
public class TargetDesignatorItem extends Item {
    public TargetDesignatorItem(Properties props) {
        super(props);
    }

    public static void setTarget(ItemStack stack, Vec3 t) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("HasTarget", true);
        tag.putDouble("TX", t.x);
        tag.putDouble("TY", t.y);
        tag.putDouble("TZ", t.z);
    }

    @Nullable
    public static Vec3 getTarget(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("HasTarget")) return null;
        return new Vec3(tag.getDouble("TX"), tag.getDouble("TY"), tag.getDouble("TZ"));
    }

    /** Цель из целеуказателя в руках или в инвентаре игрока. */
    @Nullable
    public static Vec3 findTarget(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof TargetDesignatorItem) {
            Vec3 t = getTarget(main);
            if (t != null) return t;
        }
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof TargetDesignatorItem) {
            Vec3 t = getTarget(off);
            if (t != null) return t;
        }
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() instanceof TargetDesignatorItem) {
                Vec3 t = getTarget(s);
                if (t != null) return t;
            }
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (!level.isClientSide) {
            Vec3 t = Vec3.atCenterOf(ctx.getClickedPos()).add(0.0, 0.5, 0.0);
            setTarget(ctx.getItemInHand(), t);
            Player player = ctx.getPlayer();
            if (player != null) {
                player.displayClientMessage(Component.translatable("msg.frontline.target_set",
                        Math.round(t.x), Math.round(t.y), Math.round(t.z)), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Vec3 t = getTarget(stack);
        if (t == null) {
            tooltip.add(Component.translatable("tooltip.frontline.no_target"));
        } else {
            tooltip.add(Component.translatable("tooltip.frontline.target",
                    Math.round(t.x), Math.round(t.y), Math.round(t.z)));
        }
    }
}
