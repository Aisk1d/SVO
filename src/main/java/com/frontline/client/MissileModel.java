package com.frontline.client;

import com.frontline.Frontline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** 3D-модель ракеты из боксов. Ось ракеты — X, нос в сторону +X (1 блок = 16 пикселей). Текстура 128x64. */
public class MissileModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Frontline.MODID, "ballistic_missile"), "main");

    private final ModelPart root;

    public MissileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();

        // корпус
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-16.0F, -3.0F, -3.0F, 28.0F, 6.0F, 6.0F), PartPose.ZERO);
        // головная часть (ступенчатый конус)
        p.addOrReplaceChild("nose1", CubeListBuilder.create().texOffs(0, 14)
                .addBox(12.0F, -2.5F, -2.5F, 4.0F, 5.0F, 5.0F), PartPose.ZERO);
        p.addOrReplaceChild("nose2", CubeListBuilder.create().texOffs(0, 26)
                .addBox(16.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.ZERO);
        p.addOrReplaceChild("nose3", CubeListBuilder.create().texOffs(0, 34)
                .addBox(19.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F), PartPose.ZERO);
        // сопло
        p.addOrReplaceChild("nozzle", CubeListBuilder.create().texOffs(0, 38)
                .addBox(-19.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F), PartPose.ZERO);
        // стабилизаторы (крест)
        p.addOrReplaceChild("finV", CubeListBuilder.create().texOffs(20, 14)
                .addBox(-18.0F, -7.0F, -0.5F, 7.0F, 14.0F, 1.0F), PartPose.ZERO);
        p.addOrReplaceChild("finH", CubeListBuilder.create().texOffs(40, 14)
                .addBox(-18.0F, -0.5F, -7.0F, 7.0F, 1.0F, 14.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float r, float g, float b, float a) {
        root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}
