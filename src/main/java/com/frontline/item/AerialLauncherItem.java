package com.frontline.item;

import com.frontline.ModEntities;
import com.frontline.entity.DroneEntity;
import com.frontline.entity.UavEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/** Запуск FPV-дрона / разведывательного / ударного БПЛА по ПКМ. Цель берётся из целеуказателя в инвентаре. */
public class AerialLauncherItem extends Item {
    public enum Kind { FPV_DRONE, RECON_UAV, STRIKE_UAV, GERAN_DRONE }

    private final Kind kind;

    public AerialLauncherItem(Properties props, Kind kind) {
        super(props);
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        Vec3 target = TargetDesignatorItem.findTarget(player);
        Vec3 look = player.getLookAngle();
        Vec3 start = player.getEyePosition().add(look.scale(1.5));

        switch (kind) {
            case FPV_DRONE -> {
                if (!check(player, serverLevel, start, target, 20.0, 1500.0)) return InteractionResultHolder.fail(stack);
                DroneEntity drone = ModEntities.DRONE.get().create(serverLevel);
                if (drone == null) return InteractionResultHolder.fail(stack);
                drone.launchStrike(start, target, player.getUUID());
                serverLevel.addFreshEntity(drone);
            }
            case RECON_UAV -> {
                Vec3 center = target != null ? target : player.position();
                UavEntity uav = ModEntities.UAV_RECON.get().create(serverLevel);
                if (uav == null) return InteractionResultHolder.fail(stack);
                uav.launchRecon(start, center, look, player.getUUID());
                serverLevel.addFreshEntity(uav);
            }
            case STRIKE_UAV -> {
                if (!check(player, serverLevel, start, target, 60.0, 8000.0)) return InteractionResultHolder.fail(stack);
                UavEntity uav = ModEntities.UAV_STRIKE.get().create(serverLevel);
                if (uav == null) return InteractionResultHolder.fail(stack);
                uav.launchStrike(start, target, look, player.getUUID());
                serverLevel.addFreshEntity(uav);
            }
            case GERAN_DRONE -> {
                // барражирующий боеприпас сверхбольшой дальности
                if (!check(player, serverLevel, start, target, 100.0, 20000.0)) return InteractionResultHolder.fail(stack);
                UavEntity uav = ModEntities.UAV_GERAN.get().create(serverLevel);
                if (uav == null) return InteractionResultHolder.fail(stack);
                uav.launchStrike(start, target, look, player.getUUID());
                serverLevel.addFreshEntity(uav);
            }
        }

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH,
                SoundSource.PLAYERS, 1.0F, 1.4F);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private static boolean check(Player player, ServerLevel level, Vec3 start, @Nullable Vec3 target,
                                 double min, double max) {
        if (target == null) {
            player.displayClientMessage(Component.translatable("msg.frontline.no_target"), true);
            return false;
        }
        double dx = target.x - start.x;
        double dz = target.z - start.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        if (horiz < min) {
            player.displayClientMessage(Component.translatable("msg.frontline.too_close", (int) min), true);
            return false;
        }
        if (horiz > max) {
            player.displayClientMessage(Component.translatable("msg.frontline.too_far", (int) max), true);
            return false;
        }
        if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(target))) {
            player.displayClientMessage(Component.translatable("msg.frontline.out_of_border"), true);
            return false;
        }
        return true;
    }
}
