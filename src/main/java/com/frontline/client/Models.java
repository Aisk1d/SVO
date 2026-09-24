package com.frontline.client;

import com.frontline.Frontline;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/** СГЕНЕРИРОВАНО скриптом gen_models.py: 3D-модели дронов, БПЛА, перехватчика, радара и ПВО. */
public final class Models {
    private Models() {}

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(Frontline.MODID, name), "main");
    }

    public static final ModelLayerLocation FPV_DRONE_LAYER = layer("fpv_drone");
    public static final String[] FPV_DRONE_SPIN_Y = {"rotor0", "rotor1", "rotor2", "rotor3"};
    public static final String[] FPV_DRONE_SPIN_X = {};

    public static LayerDefinition fpvDroneLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -2.0F, -3.0F, 10.0F, 4.0F, 6.0F)
                .texOffs(24, 10).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 2.0F, 4.0F)
                .texOffs(32, 0).addBox(-8.0F, -2.0F, -2.0F, 3.0F, 3.0F, 4.0F)
                .texOffs(0, 30).addBox(-9.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F)
                .texOffs(0, 10).addBox(-5.0F, 2.0F, -2.0F, 8.0F, 3.0F, 4.0F)
                .texOffs(60, 10).addBox(3.0F, -8.0F, 1.0F, 1.0F, 4.0F, 1.0F)
                .texOffs(32, 36).addBox(-6.0F, 7.0F, -4.0F, 12.0F, 1.0F, 1.0F)
                .texOffs(0, 39).addBox(-6.0F, 7.0F, 3.0F, 12.0F, 1.0F, 1.0F)
                .texOffs(44, 10).addBox(3.0F, 2.0F, -4.0F, 1.0F, 5.0F, 1.0F)
                .texOffs(48, 10).addBox(3.0F, 2.0F, 3.0F, 1.0F, 5.0F, 1.0F)
                .texOffs(52, 10).addBox(-4.0F, 2.0F, -4.0F, 1.0F, 5.0F, 1.0F)
                .texOffs(56, 10).addBox(-4.0F, 2.0F, 3.0F, 1.0F, 5.0F, 1.0F)
                .texOffs(0, 17).addBox(-10.0F, -3.0F, 8.0F, 2.0F, 3.0F, 2.0F)
                .texOffs(8, 17).addBox(-10.0F, -3.0F, -10.0F, 2.0F, 3.0F, 2.0F)
                .texOffs(16, 17).addBox(8.0F, -3.0F, 8.0F, 2.0F, 3.0F, 2.0F)
                .texOffs(24, 17).addBox(8.0F, -3.0F, -10.0F, 2.0F, 3.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("arm0", CubeListBuilder.create()
                .texOffs(32, 17).addBox(-13.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-45.0), 0.0F));
        root.addOrReplaceChild("arm1", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-13.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(45.0), 0.0F));
        root.addOrReplaceChild("arm2", CubeListBuilder.create()
                .texOffs(30, 22).addBox(-13.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-135.0), 0.0F));
        root.addOrReplaceChild("arm3", CubeListBuilder.create()
                .texOffs(0, 26).addBox(-13.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(135.0), 0.0F));
        root.addOrReplaceChild("rotor0", CubeListBuilder.create()
                .texOffs(6, 30).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 1.0F, 2.0F)
                .texOffs(30, 26).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(-9.0F, -4.0F, -9.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("rotor1", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 1.0F, 2.0F)
                .texOffs(38, 26).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(-9.0F, -4.0F, 9.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("rotor2", CubeListBuilder.create()
                .texOffs(32, 33).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 1.0F, 2.0F)
                .texOffs(46, 26).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(9.0F, -4.0F, -9.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("rotor3", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 1.0F, 2.0F)
                .texOffs(54, 26).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(9.0F, -4.0F, 9.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static final ModelLayerLocation RECON_UAV_LAYER = layer("recon_uav");
    public static final String[] RECON_UAV_SPIN_Y = {};
    public static final String[] RECON_UAV_SPIN_X = {"prop0"};

    public static LayerDefinition reconUavLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 60).addBox(-16.0F, -3.0F, -3.0F, 30.0F, 6.0F, 6.0F)
                .texOffs(88, 60).addBox(-21.0F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F)
                .texOffs(64, 73).addBox(-24.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F)
                .texOffs(74, 73).addBox(14.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F)
                .texOffs(106, 60).addBox(26.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F)
                .texOffs(0, 73).addBox(-14.0F, 3.0F, -2.0F, 4.0F, 3.0F, 4.0F)
                .texOffs(110, 73).addBox(-15.0F, 4.0F, -1.0F, 1.0F, 1.0F, 2.0F)
                .texOffs(16, 73).addBox(-6.0F, -5.0F, -2.0F, 8.0F, 2.0F, 4.0F)
                .texOffs(80, 30).addBox(-9.0F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("wing", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.0F, -1.0F, -28.0F, 9.0F, 2.0F, 28.0F)
                .texOffs(0, 30).addBox(-6.0F, -1.0F, 0.0F, 9.0F, 2.0F, 28.0F)
                .texOffs(40, 73).addBox(-3.0F, -5.0F, 28.0F, 5.0F, 4.0F, 1.0F)
                .texOffs(52, 73).addBox(-3.0F, -5.0F, -29.0F, 5.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("tailR", CubeListBuilder.create()
                .texOffs(102, 30).addBox(-4.0F, -12.0F, -1.0F, 7.0F, 12.0F, 1.0F)
                , PartPose.offsetAndRotation(24.0F, -1.0F, 0.0F, (float) Math.toRadians(-35.0), 0.0F, 0.0F));
        root.addOrReplaceChild("tailL", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-4.0F, -12.0F, -1.0F, 7.0F, 12.0F, 1.0F)
                , PartPose.offsetAndRotation(24.0F, -1.0F, 0.0F, (float) Math.toRadians(35.0), 0.0F, 0.0F));
        root.addOrReplaceChild("prop0", CubeListBuilder.create()
                .texOffs(74, 30).addBox(-1.0F, -8.0F, -1.0F, 1.0F, 16.0F, 2.0F)
                .texOffs(102, 73).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(30.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final ModelLayerLocation STRIKE_UAV_LAYER = layer("strike_uav");
    public static final String[] STRIKE_UAV_SPIN_Y = {};
    public static final String[] STRIKE_UAV_SPIN_X = {"prop0"};

    public static LayerDefinition strikeUavLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 92).addBox(-16.0F, -2.0F, -2.0F, 26.0F, 4.0F, 4.0F)
                .texOffs(60, 92).addBox(-21.0F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F)
                .texOffs(0, 100).addBox(-24.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F)
                .texOffs(78, 92).addBox(10.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                .texOffs(82, 64).addBox(-14.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F)
                .texOffs(0, 0).addBox(7.0F, -1.0F, -17.0F, 9.0F, 2.0F, 34.0F)
                .texOffs(94, 92).addBox(9.0F, -8.0F, 16.0F, 7.0F, 7.0F, 1.0F)
                .texOffs(110, 92).addBox(9.0F, -8.0F, -17.0F, 7.0F, 7.0F, 1.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("wingR", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-6.0F, -1.0F, 0.0F, 12.0F, 2.0F, 26.0F)
                , PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(50.0), 0.0F));
        root.addOrReplaceChild("wingL", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-6.0F, -1.0F, -26.0F, 12.0F, 2.0F, 26.0F)
                , PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-50.0), 0.0F));
        root.addOrReplaceChild("prop0", CubeListBuilder.create()
                .texOffs(76, 64).addBox(-1.0F, -6.0F, -1.0F, 1.0F, 12.0F, 2.0F)
                .texOffs(10, 100).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(15.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final ModelLayerLocation GERAN_DRONE_LAYER = layer("geran_drone");
    public static final String[] GERAN_DRONE_SPIN_Y = {};
    public static final String[] GERAN_DRONE_SPIN_X = {"prop0"};

    public static LayerDefinition geranDroneLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 58).addBox(-19.0F, -3.0F, -3.0F, 34.0F, 6.0F, 6.0F)
                .texOffs(108, 58).addBox(-23.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                .texOffs(0, 70).addBox(15.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                .texOffs(56, 70).addBox(-24.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                .texOffs(74, 29).addBox(-13.0F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F)
                .texOffs(16, 70).addBox(-2.0F, -5.0F, -2.0F, 6.0F, 2.0F, 4.0F)
                .texOffs(64, 70).addBox(19.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("wing", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, -1.0F, -30.0F, 10.0F, 2.0F, 27.0F)
                .texOffs(0, 29).addBox(-8.0F, -1.0F, 3.0F, 10.0F, 2.0F, 27.0F)
                .texOffs(36, 70).addBox(-3.0F, -5.0F, 29.0F, 4.0F, 4.0F, 1.0F)
                .texOffs(46, 70).addBox(-3.0F, -5.0F, -30.0F, 4.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("finR", CubeListBuilder.create()
                .texOffs(80, 58).addBox(-4.0F, -10.0F, -1.0F, 6.0F, 10.0F, 1.0F)
                , PartPose.offsetAndRotation(18.0F, -2.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(20.0)));
        root.addOrReplaceChild("finL", CubeListBuilder.create()
                .texOffs(94, 58).addBox(-4.0F, -10.0F, -1.0F, 6.0F, 10.0F, 1.0F)
                , PartPose.offsetAndRotation(18.0F, -2.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-20.0)));
        root.addOrReplaceChild("prop0", CubeListBuilder.create()
                .texOffs(96, 29).addBox(-1.0F, -7.0F, -1.0F, 1.0F, 14.0F, 2.0F)
                .texOffs(74, 70).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(21.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final ModelLayerLocation INTERCEPTOR_LAYER = layer("interceptor");
    public static final String[] INTERCEPTOR_SPIN_Y = {};
    public static final String[] INTERCEPTOR_SPIN_X = {};

    public static LayerDefinition interceptorLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 13).addBox(-14.0F, -2.0F, -2.0F, 32.0F, 4.0F, 4.0F)
                .texOffs(72, 13).addBox(-18.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                .texOffs(88, 13).addBox(-21.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F)
                .texOffs(54, 0).addBox(10.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F)
                .texOffs(82, 0).addBox(-2.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F)
                .texOffs(0, 0).addBox(11.0F, -6.0F, -1.0F, 7.0F, 12.0F, 1.0F)
                .texOffs(16, 0).addBox(11.0F, -1.0F, -6.0F, 7.0F, 1.0F, 12.0F)
                .texOffs(98, 0).addBox(-11.0F, -4.0F, -1.0F, 3.0F, 8.0F, 1.0F)
                .texOffs(106, 0).addBox(-11.0F, -1.0F, -4.0F, 3.0F, 1.0F, 8.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static final ModelLayerLocation RADAR_LAYER = layer("radar");
    public static final String[] RADAR_SPIN_Y = {};
    public static final String[] RADAR_SPIN_X = {};

    public static LayerDefinition radarLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("antenna", CubeListBuilder.create()
                .texOffs(44, 42).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 10.0F, 4.0F)
                .texOffs(60, 42).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F)
                .texOffs(0, 0).addBox(-5.0F, -22.0F, -14.0F, 2.0F, 14.0F, 28.0F)
                .texOffs(0, 42).addBox(-3.0F, -22.0F, -2.0F, 2.0F, 14.0F, 4.0F)
                .texOffs(60, 0).addBox(-3.0F, -16.0F, -14.0F, 2.0F, 2.0F, 28.0F)
                .texOffs(92, 42).addBox(-8.0F, -15.0F, -2.0F, 3.0F, 2.0F, 4.0F)
                .texOffs(12, 42).addBox(2.0F, -14.0F, -5.0F, 6.0F, 6.0F, 10.0F)
                .texOffs(106, 42).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static final ModelLayerLocation AA_RACK_LAYER = layer("aa_rack");
    public static final String[] AA_RACK_SPIN_Y = {};
    public static final String[] AA_RACK_SPIN_X = {};

    public static LayerDefinition aaRackLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-6.0F, -2.0F, -8.0F, 12.0F, 2.0F, 16.0F)
                .texOffs(56, 24).addBox(-2.0F, -8.0F, -11.0F, 4.0F, 6.0F, 2.0F)
                .texOffs(68, 24).addBox(-2.0F, -8.0F, 9.0F, 4.0F, 6.0F, 2.0F)
                , PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("rack", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -1.0F, -11.0F, 2.0F, 2.0F, 22.0F)
                .texOffs(48, 0).addBox(2.0F, -3.0F, -8.0F, 2.0F, 6.0F, 16.0F)
                .texOffs(0, 42).addBox(-14.0F, -2.0F, -8.0F, 24.0F, 4.0F, 4.0F)
                .texOffs(56, 42).addBox(-15.0F, -2.0F, -8.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(66, 42).addBox(10.0F, -2.0F, -8.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(0, 50).addBox(-14.0F, -2.0F, -4.0F, 24.0F, 4.0F, 4.0F)
                .texOffs(56, 50).addBox(-15.0F, -2.0F, -4.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(66, 50).addBox(10.0F, -2.0F, -4.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(0, 58).addBox(-14.0F, -2.0F, 0.0F, 24.0F, 4.0F, 4.0F)
                .texOffs(56, 58).addBox(-15.0F, -2.0F, 0.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(66, 58).addBox(10.0F, -2.0F, 0.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(0, 66).addBox(-14.0F, -2.0F, 4.0F, 24.0F, 4.0F, 4.0F)
                .texOffs(56, 66).addBox(-15.0F, -2.0F, 4.0F, 1.0F, 4.0F, 4.0F)
                .texOffs(66, 66).addBox(10.0F, -2.0F, 4.0F, 1.0F, 4.0F, 4.0F)
                , PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(50.0)));
        return LayerDefinition.create(mesh, 128, 128);
    }

}
