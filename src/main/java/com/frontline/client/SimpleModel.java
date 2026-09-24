package com.frontline.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/** Универсальная модель: рисует все части и крутит винты (rotor* — вокруг вертикали, prop* — вокруг оси корпуса). */
public class SimpleModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart root;
    private final List<ModelPart> spinY = new ArrayList<>();
    private final List<ModelPart> spinX = new ArrayList<>();

    public SimpleModel(ModelPart root, String[] spinYNames, String[] spinXNames) {
        this.root = root;
        for (String n : spinYNames) spinY.add(root.getChild(n));
        for (String n : spinXNames) spinX.add(root.getChild(n));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        int i = 0;
        for (ModelPart p : spinY) {
            p.yRot = (i++ % 2 == 0 ? 1.0F : -1.0F) * ageInTicks * 2.4F;
        }
        for (ModelPart p : spinX) {
            p.xRot = ageInTicks * 3.0F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float r, float g, float b, float a) {
        root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}
