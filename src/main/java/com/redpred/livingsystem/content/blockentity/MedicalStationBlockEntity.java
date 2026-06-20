package com.redpred.livingsystem.content.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 医疗工作站方块实体（见开发文档 §16.2）。阶段一为空壳，仅验证方块实体注册管线；
 * 设备运行状态、库存与治疗逻辑在后续阶段实现。
 */
public class MedicalStationBlockEntity extends BlockEntity {

    public MedicalStationBlockEntity(BlockPos pos, BlockState state) {
        super(MedicalBlockEntities.MEDICAL_STATION.get(), pos, state);
    }
}
