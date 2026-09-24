package com.frontline.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Общие лёгкие эффекты полёта (звук двигателя + частицы выхлопа) для ракет и БПЛА.
 * Вызывать ТОЛЬКО из клиентской ветки tick() (level().isClientSide).
 *
 * Ради производительности всё дозируется по времени (раз в N тиков), а не идёт непрерывным
 * потоком: при волне из десятков дронов/ракет это не даёт заметной просадки FPS/TPS.
 * Звук проигрывается через Level#playLocalSound — это "локальный" звук без сетевого трафика,
 * который на выделенном сервере ничего не делает (безопасно вызывать из общего кода).
 */
public final class FlightFx {
    private FlightFx() {
    }

    /** Короткий импульс "звука двигателя" через равные промежутки — дешёвая имитация цикличного гула. */
    public static void engineSound(Entity e, SoundEvent sound, float volume, float pitch, int intervalTicks) {
        if (e.tickCount % Math.max(1, intervalTicks) == 0) {
            e.level().playLocalSound(e.getX(), e.getY(), e.getZ(), sound, SoundSource.NEUTRAL, volume, pitch, false);
        }
    }

    /** Частица выхлопа позади сущности по направлению движения, тоже дозированная по времени. */
    public static void exhaustTrail(Entity e, ParticleOptions particle, double behind, int intervalTicks) {
        if (e.tickCount % Math.max(1, intervalTicks) != 0) return;
        Vec3 v = e.position().subtract(e.xo, e.yo, e.zo);
        if (v.lengthSqr() < 1.0E-6) return;
        Vec3 tail = e.position().subtract(v.normalize().scale(behind));
        e.level().addParticle(particle, tail.x, tail.y, tail.z, 0.0, 0.0, 0.0);
    }
}
