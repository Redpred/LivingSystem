package com.redpred.livingsystem.content.block;

import com.mojang.serialization.MapCodec;
import com.redpred.livingsystem.content.blockentity.MedicalStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;

/**
 * 医疗工作站方块（见开发文档 §16.2）。带方块实体的储存设施：右键打开 9 格医疗物资库存界面。
 * 完整生产/治疗设备逻辑（机器角色、配方、外部能源接入）在后续阶段扩展。
 */
public class MedicalStationBlock extends BaseEntityBlock {

    public static final MapCodec<MedicalStationBlock> CODEC = simpleCodec(MedicalStationBlock::new);

    public MedicalStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MedicalStationBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MedicalStationBlockEntity station) {
            player.openMenu(station, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof MedicalStationBlockEntity station) {
            station.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
