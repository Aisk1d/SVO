package com.frontline;

import com.frontline.entity.DroneEntity;
import com.frontline.entity.UavEntity;
import com.frontline.item.TargetDesignatorItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("frontline")
                // /frontline wave <1-40> (оператор)
                .then(Commands.literal("wave")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 40))
                                .executes(ctx -> WaveCommand.run(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "count")))))
                // /frontline drones <1-20> (оператор): вражеские FPV-дроны охотятся за тобой
                .then(Commands.literal("drones")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 20))
                                .executes(ctx -> spawnDrones(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "count")))))
                // /frontline uavs <1-10> (оператор): ударные БПЛА издалека летят на твою позицию
                .then(Commands.literal("uavs")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 10))
                                .executes(ctx -> spawnUavs(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "count")))))
                // /frontline target <x> <y> <z>
                .then(Commands.literal("target")
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(ctx -> setTarget(ctx.getSource(),
                                        Vec3Argument.getVec3(ctx, "pos"))))));
    }

    private static int setTarget(CommandSourceStack src, Vec3 pos) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof TargetDesignatorItem)) {
            stack = player.getOffhandItem();
        }
        if (!(stack.getItem() instanceof TargetDesignatorItem)) {
            src.sendFailure(Component.translatable("msg.frontline.hold_designator"));
            return 0;
        }
        TargetDesignatorItem.setTarget(stack, pos);
        src.sendSuccess(() -> Component.translatable("msg.frontline.target_set",
                Math.round(pos.x), Math.round(pos.y), Math.round(pos.z)), false);
        return 1;
    }

    private static int spawnDrones(CommandSourceStack src, int count) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        RandomSource rand = level.random;
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double dist = 30 + rand.nextInt(20);
            Vec3 pos = new Vec3(player.getX() + Math.cos(angle) * dist,
                    player.getY() + 15 + rand.nextInt(10),
                    player.getZ() + Math.sin(angle) * dist);
            DroneEntity drone = ModEntities.DRONE.get().create(level);
            if (drone == null) continue;
            drone.launchHunt(pos);
            level.addFreshEntity(drone);
            spawned++;
        }
        int n = spawned;
        src.sendSuccess(() -> Component.translatable("msg.frontline.drones", n), true);
        return spawned;
    }

    private static int spawnUavs(CommandSourceStack src, int count) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        RandomSource rand = level.random;
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            Vec3 start = new Vec3(player.getX() + Math.cos(angle) * 220,
                    player.getY() + 70 + rand.nextInt(20),
                    player.getZ() + Math.sin(angle) * 220);
            Vec3 heading = player.position().subtract(start);
            UavEntity uav = ModEntities.UAV_STRIKE.get().create(level);
            if (uav == null) continue;
            uav.launchStrike(start, player.position(), heading, null);
            level.addFreshEntity(uav);
            spawned++;
        }
        int n = spawned;
        src.sendSuccess(() -> Component.translatable("msg.frontline.uavs", n), true);
        return spawned;
    }
}
