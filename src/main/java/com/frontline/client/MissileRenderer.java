package com.frontline.client;

import com.frontline.Frontline;
import com.frontline.entity.MissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MissileRenderer extends EntityRenderer<MissileEntity> {
    private final ResourceLocation texture;
    private final float scale;
    private final MissileModel<MissileEntity> model;

    public MissileRenderer(EntityRendererProvider.Context context) {
        this(context, new ResourceLocation(Frontline.MODID, "textures/entity/missile.png"), 1.0F);
    }

    public MissileRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float scale) {
        super(context);
        this.texture = texture;
        this.scale = scale;
        this.model = new MissileModel<>(context.bakeLayer(MissileModel.LAYER));
    }

    @Override
    public void render(MissileEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        // как у стрелы: разворот по курсу (yaw) и тангажу (pitch), модель смотрит носом в +X
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MissileEntity entity) {
        return texture;
    }
}
