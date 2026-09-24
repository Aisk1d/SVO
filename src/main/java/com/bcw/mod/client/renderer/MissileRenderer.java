package com.bcw.mod.client.renderer;

import com.bcw.mod.client.model.MissileModel;
import com.bcw.mod.entity.AbstractMissileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Shared renderer for every missile family. The GeckoLib model handles mesh + texture;
 * this class is responsible for aligning the model's pitch/yaw to the entity's real
 * flight vector every frame (interpolated for smoothness) and scaling per missile type.
 */
public class MissileRenderer extends GeoEntityRenderer<AbstractMissileEntity> {

    public MissileRenderer(EntityRendererProvider.Context context, String missileId) {
        super(context, new MissileModel(missileId));
        this.shadowRadius = 0.35F;
    }

    @Override
    public float getMotionAnimsAmount(AbstractMissileEntity entity) {
        return 1.0F;
    }

    @Override
    protected float getDeathMaxRotation(AbstractMissileEntity entity) {
        return 0.0F;
    }

    /**
     * Interpolates pitch/yaw between the entity's previous and current tick rotation so
     * fast-moving missiles don't visually "snap" between frames at high tick speeds.
     */
    public static float interpolateYaw(AbstractMissileEntity entity, float partialTicks) {
        return Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
    }

    public static float interpolatePitch(AbstractMissileEntity entity, float partialTicks) {
        return Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
    }
}
