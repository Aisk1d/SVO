package com.bcw.mod.client;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.client.renderer.MissileRenderer;
import com.bcw.mod.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BallisticWarfareMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ZIRCON.get(), ctx -> new MissileRenderer(ctx, "zircon"));
        event.registerEntityRenderer(ModEntities.TOMAHAWK.get(), ctx -> new MissileRenderer(ctx, "tomahawk"));
        event.registerEntityRenderer(ModEntities.SARMAT.get(), ctx -> new MissileRenderer(ctx, "sarmat"));
    }

    private ClientSetup() {}
}
