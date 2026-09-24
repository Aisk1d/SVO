package com.frontline.block;

import com.frontline.ModBlockEntities;
import com.frontline.entity.Trackable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Радарная станция. ПКМ пустой рукой — список целей. Выдаёт сигнал редстоуна, если в небе есть чужие цели. */
public class RadarBlock extends BaseEntityBlock {
    public static final BooleanProperty ALERT = BooleanProperty.create("alert");

    public RadarBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(ALERT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ALERT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.RADAR.get(), RadarBlockEntity::serverTick);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(ALERT) ? 15 : 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null && level.getBlockEntity(pos) instanceof RadarBlockEntity radar) {
            radar.setOwner(placer.getUUID());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof RadarBlockEntity radar)) return InteractionResult.PASS;

        List<Entity> contacts = RadarBlockEntity.scan(level, pos);
        if (contacts.isEmpty()) {
            player.sendSystemMessage(Component.translatable("msg.frontline.radar_clear"));
            return InteractionResult.SUCCESS;
        }

        Vec3 center = Vec3.atCenterOf(pos);
        contacts.sort(Comparator.comparingDouble((Entity e) -> e.distanceToSqr(center)));
        player.sendSystemMessage(Component.translatable("msg.frontline.radar_header", contacts.size()));

        int shown = 0;
        for (Entity e : contacts) {
            if (shown++ >= 6) break;
            double dx = e.getX() - center.x;
            double dz = e.getZ() - center.z;
            int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
            int bearing = (int) Math.round((Math.toDegrees(Math.atan2(dx, -dz)) + 360.0) % 360.0);
            int alt = (int) Math.round(e.getY() - pos.getY());
            boolean friend = Trackable.friendly(((Trackable) e).getOwnerUuid(), radar.getOwner());
            player.sendSystemMessage(Component.translatable(
                    friend ? "msg.frontline.radar_friendly" : "msg.frontline.radar_contact",
                    e.getType().getDescription(), dist, bearing, String.format(Locale.ROOT, "%+d", alt)));
        }
        return InteractionResult.SUCCESS;
    }
}
