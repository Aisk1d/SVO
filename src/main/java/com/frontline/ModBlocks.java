package com.frontline;

import com.frontline.block.AaSiteBlock;
import com.frontline.block.MissileLauncherBlock;
import com.frontline.block.RadarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Frontline.MODID);

    public static final RegistryObject<Block> SANDBAGS = BLOCKS.register("sandbags",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND).strength(1.5F, 10.0F).sound(SoundType.SAND)));

    public static final RegistryObject<Block> LAUNCHER = BLOCKS.register("missile_launcher",
            () -> new MissileLauncherBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(5.0F, 30.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> RADAR = BLOCKS.register("radar",
            () -> new RadarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(4.0F, 20.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> AA_SITE = BLOCKS.register("aa_site",
            () -> new AaSiteBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN).strength(4.0F, 20.0F).sound(SoundType.METAL).noOcclusion()));
}
