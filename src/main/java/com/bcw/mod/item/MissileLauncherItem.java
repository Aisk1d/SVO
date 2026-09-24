package com.bcw.mod.item;

import com.bcw.mod.entity.AbstractMissileEntity;
import com.bcw.mod.entity.missiles.SarmatEntity;
import com.bcw.mod.entity.missiles.TomahawkEntity;
import com.bcw.mod.entity.missiles.ZirconEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MissileLauncherItem extends Item {

    public enum Kind { ZIRCON, TOMAHAWK, SARMAT }

    private static final double MAX_TARGET_DISTANCE = 2500.0;

    private final Kind kind;

    public MissileLauncherItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, Player player, InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);

        HitResult hit = player.pick(MAX_TARGET_DISTANCE, 0.0F, false);
        Vec3 targetPos;
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            targetPos = blockHit.getLocation();
        } else {
            targetPos = player.getEyePosition().add(player.getLookAngle().scale(MAX_TARGET_DISTANCE));
        }

        if (!level.isClientSide) {
            Vec3 launchPos = player.getEyePosition().add(player.getLookAngle().scale(1.5)).add(0, 1.0, 0);
            AbstractMissileEntity missile = createMissile(level, launchPos, targetPos);
            if (missile != null) {
                level.addFreshEntity(missile);
                level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.get(),
                        SoundSource.HOSTILE, 1.0F, 0.6F);
            }
        }

        player.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private AbstractMissileEntity createMissile(Level level, Vec3 launchPos, Vec3 targetPos) {
        return switch (kind) {
            case ZIRCON -> ZirconEntity.create(level, launchPos.x, launchPos.y, launchPos.z,
                    targetPos.x, targetPos.y, targetPos.z);
            case TOMAHAWK -> TomahawkEntity.create(level, launchPos.x, launchPos.y, launchPos.z,
                    targetPos.x, targetPos.y, targetPos.z);
            case SARMAT -> SarmatEntity.create(level, launchPos.x, launchPos.y, launchPos.z,
                    targetPos.x, targetPos.y, targetPos.z);
        };
    }
}
