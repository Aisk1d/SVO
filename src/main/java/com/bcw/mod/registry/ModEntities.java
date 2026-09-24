package com.bcw.mod.registry;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.entity.missiles.SarmatEntity;
import com.bcw.mod.entity.missiles.TomahawkEntity;
import com.bcw.mod.entity.missiles.ZirconEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BallisticWarfareMod.MOD_ID);

    public static final RegistryObject<EntityType<ZirconEntity>> ZIRCON = ENTITY_TYPES.register(
            "zircon",
            () -> EntityType.Builder.<ZirconEntity>of(ZirconEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("zircon")
    );

    public static final RegistryObject<EntityType<TomahawkEntity>> TOMAHAWK = ENTITY_TYPES.register(
            "tomahawk",
            () -> EntityType.Builder.<TomahawkEntity>of(TomahawkEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("tomahawk")
    );

    public static final RegistryObject<EntityType<SarmatEntity>> SARMAT = ENTITY_TYPES.register(
            "sarmat",
            () -> EntityType.Builder.<SarmatEntity>of(SarmatEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .build("sarmat")
    );

    private ModEntities() {}
}
