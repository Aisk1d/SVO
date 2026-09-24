package com.frontline;

import com.frontline.block.AaSiteBlockEntity;
import com.frontline.block.MissileLauncherBlockEntity;
import com.frontline.block.RadarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Frontline.MODID);

    public static final RegistryObject<BlockEntityType<MissileLauncherBlockEntity>> LAUNCHER =
            BLOCK_ENTITIES.register("missile_launcher",
                    () -> BlockEntityType.Builder.of(MissileLauncherBlockEntity::new, ModBlocks.LAUNCHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR =
            BLOCK_ENTITIES.register("radar",
                    () -> BlockEntityType.Builder.of(RadarBlockEntity::new, ModBlocks.RADAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<AaSiteBlockEntity>> AA_SITE =
            BLOCK_ENTITIES.register("aa_site",
                    () -> BlockEntityType.Builder.of(AaSiteBlockEntity::new, ModBlocks.AA_SITE.get()).build(null));
}
