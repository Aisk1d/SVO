package com.frontline;

import com.frontline.entity.DroneEntity;
import com.frontline.entity.InterceptorEntity;
import com.frontline.entity.MissileEntity;
import com.frontline.entity.UavEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Frontline.MODID);

    public static final RegistryObject<EntityType<MissileEntity>> MISSILE = ENTITIES.register("ballistic_missile",
            () -> EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).fireImmune()
                    .build("ballistic_missile"));

    /** "Орешник" — тяжёлая манёвренная ракета средней дальности, крутая настильная траектория, самая большая боеголовка. */
    public static final RegistryObject<EntityType<MissileEntity>> ORESHNIK = ENTITIES.register("oreshnik_missile",
            () -> EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F).clientTrackingRange(12).updateInterval(1).fireImmune()
                    .build("oreshnik_missile"));

    /** "Буревестник" — крылатая ракета сверхбольшой дальности с низким горизонтальным профилем полёта. */
    public static final RegistryObject<EntityType<MissileEntity>> BUREVESTNIK = ENTITIES.register("burevestnik_missile",
            () -> EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).clientTrackingRange(14).updateInterval(1).fireImmune()
                    .build("burevestnik_missile"));

    public static final RegistryObject<EntityType<DroneEntity>> DRONE = ENTITIES.register("fpv_drone",
            () -> EntityType.Builder.<DroneEntity>of(DroneEntity::new, MobCategory.MISC)
                    .sized(0.7F, 0.35F).clientTrackingRange(10).updateInterval(1).fireImmune()
                    .build("fpv_drone"));

    public static final RegistryObject<EntityType<UavEntity>> UAV_RECON = ENTITIES.register("recon_uav",
            () -> EntityType.Builder.<UavEntity>of((type, level) -> new UavEntity(type, level, false), MobCategory.MISC)
                    .sized(1.4F, 0.5F).clientTrackingRange(12).updateInterval(1).fireImmune()
                    .build("recon_uav"));

    public static final RegistryObject<EntityType<UavEntity>> UAV_STRIKE = ENTITIES.register("strike_uav",
            () -> EntityType.Builder.<UavEntity>of((type, level) -> new UavEntity(type, level, true), MobCategory.MISC)
                    .sized(1.6F, 0.5F).clientTrackingRange(12).updateInterval(1).fireImmune()
                    .build("strike_uav"));

    /** "Герань" — барражирующий боеприпас (дельта-крыло, мопед-двигатель) сверхбольшой дальности. */
    public static final RegistryObject<EntityType<UavEntity>> UAV_GERAN = ENTITIES.register("geran_drone",
            () -> EntityType.Builder.<UavEntity>of((type, level) -> new UavEntity(type, level, true), MobCategory.MISC)
                    .sized(1.5F, 0.5F).clientTrackingRange(14).updateInterval(1).fireImmune()
                    .build("geran_drone"));

    public static final RegistryObject<EntityType<InterceptorEntity>> INTERCEPTOR = ENTITIES.register("interceptor",
            () -> EntityType.Builder.<InterceptorEntity>of(InterceptorEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).fireImmune()
                    .build("interceptor"));
}
