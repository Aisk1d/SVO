package com.bcw.mod.registry;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.block.RadarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BallisticWarfareMod.MOD_ID);

    public static final RegistryObject<Block> RADAR_STATION = BLOCKS.register(
            "radar_station",
            () -> new RadarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion())
    );

    private ModBlocks() {}
}
