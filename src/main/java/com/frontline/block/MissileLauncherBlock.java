package com.frontline.block;

import com.frontline.ModItems;
import com.frontline.item.TargetDesignatorItem;
import com.frontline.missile.BallisticTrajectory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Пусковая установка.
 *  - ПКМ ракетой — зарядить.
 *  - ПКМ целеуказателем — записать в установку цель.
 *  - ПКМ пустой рукой — показать статус.
 *  - Сигнал редстоуна — пуск.
 */
public class MissileLauncherBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public MissileLauncherBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MissileLauncherBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        MissileLauncherBlockEntity.MissileKind heldKind = kindOf(held);
        boolean isMissile = heldKind != null;
        boolean isDesignator = held.getItem() instanceof TargetDesignatorItem;
        if (!(isMissile || isDesignator || held.isEmpty())) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof MissileLauncherBlockEntity launcher)) {
            return InteractionResult.PASS;
        }

        if (isMissile) {
            if (launcher.isLoaded()) {
                player.displayClientMessage(Component.translatable("msg.frontline.already_loaded"), true);
            } else {
                launcher.setLoaded(true, heldKind);
                if (!player.getAbilities().instabuild) held.shrink(1);
                player.displayClientMessage(Component.translatable("msg.frontline.loaded"), true);
            }
        } else if (isDesignator) {
            MissileLauncherBlockEntity.MissileKind kind = launcher.getLoadedKind();
            Vec3 target = TargetDesignatorItem.getTarget(held);
            Vec3 start = Vec3.atBottomCenterOf(pos.above());
            if (target == null) {
                player.displayClientMessage(Component.translatable("msg.frontline.no_target"), true);
            } else {
                BallisticTrajectory.Result r = BallisticTrajectory.validate(start, target, kind.minRange, kind.maxRange);
                if (r == BallisticTrajectory.Result.TOO_CLOSE) {
                    player.displayClientMessage(Component.translatable("msg.frontline.too_close",
                            (int) kind.minRange), true);
                } else if (r == BallisticTrajectory.Result.TOO_FAR) {
                    player.displayClientMessage(Component.translatable("msg.frontline.too_far",
                            (int) kind.maxRange), true);
                } else if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(target))) {
                    player.displayClientMessage(Component.translatable("msg.frontline.out_of_border"), true);
                } else {
                    launcher.setTarget(target);
                    player.displayClientMessage(Component.translatable("msg.frontline.programmed",
                            Math.round(target.x), Math.round(target.y), Math.round(target.z)), true);
                }
            }
        } else {
            Vec3 t = launcher.getTarget();
            Component loaded = Component.translatable(launcher.isLoaded() ? "msg.frontline.yes" : "msg.frontline.no");
            Component tgt = t == null
                    ? Component.translatable("msg.frontline.none")
                    : Component.literal(Math.round(t.x) + " " + Math.round(t.y) + " " + Math.round(t.z));
            player.displayClientMessage(Component.translatable("msg.frontline.status", loaded, tgt), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private static MissileLauncherBlockEntity.MissileKind kindOf(ItemStack stack) {
        if (stack.is(ModItems.BALLISTIC_MISSILE.get())) return MissileLauncherBlockEntity.MissileKind.BALLISTIC;
        if (stack.is(ModItems.ORESHNIK_MISSILE.get())) return MissileLauncherBlockEntity.MissileKind.ORESHNIK;
        if (stack.is(ModItems.BUREVESTNIK_MISSILE.get())) return MissileLauncherBlockEntity.MissileKind.BUREVESTNIK;
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null && level.getBlockEntity(pos) instanceof MissileLauncherBlockEntity launcher) {
            launcher.setOwner(placer.getUUID());
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
            if (powered && level instanceof ServerLevel serverLevel
                    && level.getBlockEntity(pos) instanceof MissileLauncherBlockEntity launcher) {
                launcher.tryLaunch(serverLevel, pos);
            }
        }
    }
}
