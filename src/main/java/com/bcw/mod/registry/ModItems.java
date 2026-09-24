package com.bcw.mod.registry;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.item.MissileLauncherItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BallisticWarfareMod.MOD_ID);

    public static final RegistryObject<Item> RADAR_STATION_ITEM = ITEMS.register(
            "radar_station",
            () -> new BlockItem(ModBlocks.RADAR_STATION.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ZIRCON_LAUNCHER = ITEMS.register(
            "zircon_launcher",
            () -> new MissileLauncherItem(MissileLauncherItem.Kind.ZIRCON, new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> TOMAHAWK_LAUNCHER = ITEMS.register(
            "tomahawk_launcher",
            () -> new MissileLauncherItem(MissileLauncherItem.Kind.TOMAHAWK, new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> SARMAT_LAUNCHER = ITEMS.register(
            "sarmat_launcher",
            () -> new MissileLauncherItem(MissileLauncherItem.Kind.SARMAT, new Item.Properties().stacksTo(1))
    );

    private ModItems() {}
}
