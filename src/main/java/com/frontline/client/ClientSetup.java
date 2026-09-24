package com.frontline.client;

import com.frontline.Frontline;
import com.frontline.ModBlockEntities;
import com.frontline.ModEntities;
import com.frontline.entity.DroneEntity;
import com.frontline.entity.InterceptorEntity;
import com.frontline.entity.UavEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Frontline.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    private static ResourceLocation tex(String name) {
        return new ResourceLocation(Frontline.MODID, "textures/entity/" + name + ".png");
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MISSILE.get(), MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.ORESHNIK.get(),
                ctx -> new MissileRenderer(ctx, tex("oreshnik_missile"), 1.35F));
        event.registerEntityRenderer(ModEntities.BUREVESTNIK.get(),
                ctx -> new MissileRenderer(ctx, tex("burevestnik_missile"), 1.0F));

        event.registerEntityRenderer(ModEntities.DRONE.get(), ctx -> new SimpleEntityRenderer<DroneEntity>(ctx,
                new SimpleModel<DroneEntity>(ctx.bakeLayer(Models.FPV_DRONE_LAYER),
                        Models.FPV_DRONE_SPIN_Y, Models.FPV_DRONE_SPIN_X),
                tex("fpv_drone"), 0.6F));

        event.registerEntityRenderer(ModEntities.UAV_RECON.get(), ctx -> new SimpleEntityRenderer<UavEntity>(ctx,
                new SimpleModel<UavEntity>(ctx.bakeLayer(Models.RECON_UAV_LAYER),
                        Models.RECON_UAV_SPIN_Y, Models.RECON_UAV_SPIN_X),
                tex("recon_uav"), 0.9F));

        event.registerEntityRenderer(ModEntities.UAV_STRIKE.get(), ctx -> new SimpleEntityRenderer<UavEntity>(ctx,
                new SimpleModel<UavEntity>(ctx.bakeLayer(Models.STRIKE_UAV_LAYER),
                        Models.STRIKE_UAV_SPIN_Y, Models.STRIKE_UAV_SPIN_X),
                tex("strike_uav"), 0.9F));

        event.registerEntityRenderer(ModEntities.UAV_GERAN.get(), ctx -> new SimpleEntityRenderer<UavEntity>(ctx,
                new SimpleModel<UavEntity>(ctx.bakeLayer(Models.GERAN_DRONE_LAYER),
                        Models.GERAN_DRONE_SPIN_Y, Models.GERAN_DRONE_SPIN_X),
                tex("geran_drone"), 0.9F));

        event.registerEntityRenderer(ModEntities.INTERCEPTOR.get(), ctx -> new SimpleEntityRenderer<InterceptorEntity>(ctx,
                new SimpleModel<InterceptorEntity>(ctx.bakeLayer(Models.INTERCEPTOR_LAYER),
                        Models.INTERCEPTOR_SPIN_Y, Models.INTERCEPTOR_SPIN_X),
                tex("interceptor"), 0.6F));

        event.registerBlockEntityRenderer(ModBlockEntities.RADAR.get(), RadarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.AA_SITE.get(), AaSiteRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MissileModel.LAYER, MissileModel::createBodyLayer);
        event.registerLayerDefinition(Models.FPV_DRONE_LAYER, Models::fpvDroneLayer);
        event.registerLayerDefinition(Models.RECON_UAV_LAYER, Models::reconUavLayer);
        event.registerLayerDefinition(Models.STRIKE_UAV_LAYER, Models::strikeUavLayer);
        event.registerLayerDefinition(Models.GERAN_DRONE_LAYER, Models::geranDroneLayer);
        event.registerLayerDefinition(Models.INTERCEPTOR_LAYER, Models::interceptorLayer);
        event.registerLayerDefinition(Models.RADAR_LAYER, Models::radarLayer);
        event.registerLayerDefinition(Models.AA_RACK_LAYER, Models::aaRackLayer);
    }
}
