package com.bcw.mod.registry;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.blockentity.RadarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BallisticWarfareMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR = BLOCK_ENTITIES.register(
            "radar_station",
            () -> BlockEntityType.Builder.of(RadarBlockEntity::new, ModBlocks.RADAR_STATION.get()).build(null)
    );

    private ModBlockEntities() {}
}
