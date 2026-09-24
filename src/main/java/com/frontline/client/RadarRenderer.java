package com.frontline.client;

import com.frontline.Frontline;
import com.frontline.block.RadarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Вращающаяся антенна радара. */
public class RadarRenderer implements BlockEntityRenderer<RadarBlockEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Frontline.MODID, "textures/entity/radar.png");

    private final SimpleModel<Entity> model;

    public RadarRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SimpleModel<>(context.bakeLayer(Models.RADAR_LAYER),
                Models.RADAR_SPIN_Y, Models.RADAR_SPIN_X);
    }

    @Override
    public void render(RadarBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = be.getLevel();
        float time = level == null ? 0.0F : level.getGameTime() + partialTick;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 6.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
