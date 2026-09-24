package com.frontline.missile;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Расчёт траектории ракеты (игровая, стилизованная баллистика).
 *
 * Три фазы:
 *  1) ПОДЪЁМ   — вертикальный разгон до высоты апогея (сглаженный smoothstep).
 *  2) КРЕЙСЕР  — полёт на апогее.
 *  3) СПУСК    — ускоряющееся почти вертикальное падение на цель.
 *
 * Горизонтальное перемещение (x, z) идёт по всей траектории по кривой smoothstep:
 * ракета стартует строго вверх, наклоняется в сторону цели и входит в цель круто сверху.
 * Итоговая точка ВСЕГДА равна координатам цели, дальность не важна.
 */
public final class BallisticTrajectory {
    public static final double MIN_RANGE = 30.0;       // ближе — ракета упадёт на пусковую
    public static final double MAX_RANGE = 25_000.0;   // предел дальности (по горизонтали)

    private static final double AVG_HORIZONTAL_SPEED = 3.0; // блоков/тик (в среднем)
    private static final double ASCENT_SPEED = 2.5;         // блоков/тик при подъёме
    private static final double DESCENT_SPEED = 3.5;        // блоков/тик при спуске

    public enum Result { OK, TOO_CLOSE, TOO_FAR }

    /**
     * Игровой профиль полёта. Реальные лётно-технические характеристики не моделируются —
     * это лишь стилизованная форма траектории для геймплея и визуала.
     *
     *  HIGH_ARC     — обычная баллистика (крутой подъём/спуск), как у стандартной ракеты.
     *  STEEP_LOFTED — очень высокий и быстрый крутой подъём с почти отвесным падением
     *                 на цель (манёвренная ракета средней дальности, тип "Орешник").
     *  LOW_CRUISE   — низкий долгий горизонтальный полёт на почти постоянной высоте
     *                 с коротким пикированием в конце (крылатая ракета, тип "Буревестник").
     */
    public enum Profile { HIGH_ARC, STEEP_LOFTED, LOW_CRUISE }

    private final Vec3 start;
    private final Vec3 target;
    private final Profile profile;
    private final double apexY;
    private final int ascent;
    private final int descent;
    private final int total;

    private BallisticTrajectory(Vec3 start, Vec3 target, Profile profile) {
        this.start = start;
        this.target = target;
        this.profile = profile;

        double dx = target.x - start.x;
        double dz = target.z - start.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);

        double ascentSpeed = ASCENT_SPEED;
        double descentSpeed = DESCENT_SPEED;
        double horizSpeed = AVG_HORIZONTAL_SPEED;
        double apex;

        switch (profile) {
            case STEEP_LOFTED -> {
                // высокая, быстрая настильная траектория с крутым нырком на цель
                apex = Math.max(start.y, target.y) + Mth.clamp(horiz * 0.45, 80.0, 480.0);
                ascentSpeed = 4.0;
                descentSpeed = 6.5;
                horizSpeed = 4.5;
            }
            case LOW_CRUISE -> {
                // низкий и долгий горизонтальный полёт над рельефом, нырок в конце
                apex = Math.max(start.y, target.y) + Mth.clamp(horiz * 0.01, 12.0, 30.0);
                ascentSpeed = 1.5;
                descentSpeed = 2.5;
                horizSpeed = 5.5;
            }
            default -> apex = Math.max(start.y, target.y) + Mth.clamp(horiz * 0.3, 40.0, 300.0);
        }
        this.apexY = apex;

        this.ascent = (int) Mth.clamp((apexY - start.y) / ascentSpeed, 40.0, 240.0);
        this.descent = (int) Mth.clamp((apexY - target.y) / descentSpeed, 40.0, 240.0);
        int cruise = Math.max(20, (int) Math.ceil(horiz / horizSpeed) - (ascent + descent) / 2);
        this.total = ascent + cruise + descent;
    }

    public static BallisticTrajectory plan(Vec3 start, Vec3 target) {
        return new BallisticTrajectory(start, target, Profile.HIGH_ARC);
    }

    public static BallisticTrajectory plan(Vec3 start, Vec3 target, Profile profile) {
        return new BallisticTrajectory(start, target, profile);
    }

    public static Result validate(Vec3 start, Vec3 target) {
        return validate(start, target, MIN_RANGE, MAX_RANGE);
    }

    public static Result validate(Vec3 start, Vec3 target, double minRange, double maxRange) {
        double dx = target.x - start.x;
        double dz = target.z - start.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        if (horiz < minRange) return Result.TOO_CLOSE;
        if (horiz > maxRange) return Result.TOO_FAR;
        return Result.OK;
    }

    public int totalTicks() {
        return total;
    }

    /** Позиция ракеты через t тиков после старта. */
    public Vec3 positionAt(double t) {
        t = Mth.clamp(t, 0.0, total);
        double s = t / total;
        double h = s * s * (3.0 - 2.0 * s); // smoothstep: горизонталь

        double x = start.x + (target.x - start.x) * h;
        double z = start.z + (target.z - start.z) * h;
        double y;

        if (t <= ascent) {
            double a = t / ascent;
            y = start.y + (apexY - start.y) * (a * a * (3.0 - 2.0 * a));
        } else if (t >= total - descent) {
            double d = (t - (total - descent)) / descent;
            y = apexY - (apexY - target.y) * (d * d);
        } else {
            y = apexY;
        }
        return new Vec3(x, y, z);
    }
}
