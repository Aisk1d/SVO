package com.frontline;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;

/** /frontline wave <кол-во> — волна мобов вокруг игрока (нужны права оператора). */
public class WaveCommand {
    static int run(CommandSourceStack src, int count) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        RandomSource rand = level.random;

        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double dist = 20 + rand.nextInt(10);
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    BlockPos.containing(player.getX() + Math.cos(angle) * dist, 0,
                            player.getZ() + Math.sin(angle) * dist));

            EntityType<? extends Mob> type = (i % 4 == 3) ? EntityType.SKELETON : EntityType.ZOMBIE;
            Mob mob = type.create(level);
            if (mob == null) continue;

            mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rand.nextFloat() * 360F, 0F);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, null, null);
            mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            mob.setTarget(player);
            level.addFreshEntity(mob);
        }

        src.sendSuccess(() -> Component.translatable("msg.frontline.wave", count), true);
        return count;
    }
}
