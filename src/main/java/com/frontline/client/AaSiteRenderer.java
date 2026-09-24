package com.frontline.client;

import com.frontline.Frontline;
import com.frontline.block.AaSiteBlock;
import com.frontline.block.AaSiteBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** Пусковая рама ПВО с четырьмя контейнерами, повёрнутая по направлению установки. */
public class AaSiteRenderer implements BlockEntityRenderer<AaSiteBlockEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Frontline.MODID, "textures/entity/aa_rack.png");

    private final SimpleModel<Entity> model;

    public AaSiteRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SimpleModel<>(context.bakeLayer(Models.AA_RACK_LAYER),
                Models.AA_RACK_SPIN_Y, Models.AA_RACK_SPIN_X);
    }

    @Override
    public void render(AaSiteBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction facing = be.getBlockState().getValue(AaSiteBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 3.0 / 16.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F - facing.toYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.75F, 0.75F, 0.75F);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
