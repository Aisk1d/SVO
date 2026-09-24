package com.frontline;

import com.frontline.item.AerialLauncherItem;
import com.frontline.item.BinocularsItem;
import com.frontline.item.MedicalItem;
import com.frontline.item.RadioItem;
import com.frontline.item.TargetDesignatorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Frontline.MODID);

    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage",
            () -> new MedicalItem(new Item.Properties().stacksTo(16), 4.0F, 32));
    public static final RegistryObject<Item> MEDKIT = ITEMS.register("medkit",
            () -> new MedicalItem(new Item.Properties().stacksTo(4), 12.0F, 64));
    public static final RegistryObject<Item> BINOCULARS = ITEMS.register("binoculars",
            () -> new BinocularsItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RADIO = ITEMS.register("radio",
            () -> new RadioItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SANDBAGS = ITEMS.register("sandbags",
            () -> new BlockItem(ModBlocks.SANDBAGS.get(), new Item.Properties()));

    // --- Баллистические и крылатые ракеты ---
    public static final RegistryObject<Item> BALLISTIC_MISSILE = ITEMS.register("ballistic_missile",
            () -> new Item(new Item.Properties().stacksTo(1)));
    /** "Орешник" — тяжёлая ракета средней дальности, самая мощная боеголовка из доступных в пусковой. */
    public static final RegistryObject<Item> ORESHNIK_MISSILE = ITEMS.register("oreshnik_missile",
            () -> new Item(new Item.Properties().stacksTo(1)));
    /** "Буревестник" — крылатая ракета сверхбольшой дальности, летит низко и долго. */
    public static final RegistryObject<Item> BUREVESTNIK_MISSILE = ITEMS.register("burevestnik_missile",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TARGET_DESIGNATOR = ITEMS.register("target_designator",
            () -> new TargetDesignatorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MISSILE_LAUNCHER = ITEMS.register("missile_launcher",
            () -> new BlockItem(ModBlocks.LAUNCHER.get(), new Item.Properties()));

    // --- Дроны и БПЛА ---
    public static final RegistryObject<Item> FPV_DRONE = ITEMS.register("fpv_drone",
            () -> new AerialLauncherItem(new Item.Properties().stacksTo(4), AerialLauncherItem.Kind.FPV_DRONE));
    public static final RegistryObject<Item> RECON_UAV = ITEMS.register("recon_uav",
            () -> new AerialLauncherItem(new Item.Properties().stacksTo(2), AerialLauncherItem.Kind.RECON_UAV));
    public static final RegistryObject<Item> STRIKE_UAV = ITEMS.register("strike_uav",
            () -> new AerialLauncherItem(new Item.Properties().stacksTo(2), AerialLauncherItem.Kind.STRIKE_UAV));
    /** "Герань" — барражирующий боеприпас сверхбольшой дальности (дельта-крыло). */
    public static final RegistryObject<Item> GERAN_DRONE = ITEMS.register("geran_drone",
            () -> new AerialLauncherItem(new Item.Properties().stacksTo(2), AerialLauncherItem.Kind.GERAN_DRONE));

    // --- ПВО и радар ---
    public static final RegistryObject<Item> INTERCEPTOR = ITEMS.register("interceptor",
            () -> new Item(new Item.Properties().stacksTo(8)));
    public static final RegistryObject<Item> RADAR = ITEMS.register("radar",
            () -> new BlockItem(ModBlocks.RADAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> AA_SITE = ITEMS.register("aa_site",
            () -> new BlockItem(ModBlocks.AA_SITE.get(), new Item.Properties()));
}
