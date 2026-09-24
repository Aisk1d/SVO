package com.frontline.block;

import com.frontline.ModBlockEntities;
import com.frontline.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/** Зенитная ракетная батарея. ПКМ перехватчиком — зарядить, пустой рукой — показать боезапас. */
public class AaSiteBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AaSiteBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AaSiteBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.AA_SITE.get(), AaSiteBlockEntity::serverTick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null && level.getBlockEntity(pos) instanceof AaSiteBlockEntity site) {
            site.setOwner(placer.getUUID());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        boolean isAmmo = held.is(ModItems.INTERCEPTOR.get());
        if (!(isAmmo || held.isEmpty())) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof AaSiteBlockEntity site)) return InteractionResult.PASS;

        if (isAmmo) {
            int space = AaSiteBlockEntity.MAX_AMMO - site.getAmmo();
            if (space <= 0) {
                player.displayClientMessage(Component.translatable("msg.frontline.aa_full"), true);
            } else {
                int add = Math.min(space, held.getCount());
                site.setAmmo(site.getAmmo() + add);
                if (!player.getAbilities().instabuild) held.shrink(add);
                player.displayClientMessage(Component.translatable("msg.frontline.aa_loaded",
                        site.getAmmo(), AaSiteBlockEntity.MAX_AMMO), true);
            }
        } else {
            player.displayClientMessage(Component.translatable("msg.frontline.aa_status",
                    site.getAmmo(), AaSiteBlockEntity.MAX_AMMO), true);
        }
        return InteractionResult.SUCCESS;
    }
}
