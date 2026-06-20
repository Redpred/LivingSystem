package com.redpred.livingsystem.content.blockentity;

import com.redpred.livingsystem.bootstrap.ModContent;
import com.redpred.livingsystem.content.block.MedicalBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 方块实体类型注册（见开发文档 §16.2）。阶段一注册一个代表性占位类型。
 */
public final class MedicalBlockEntities {

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MedicalStationBlockEntity>> MEDICAL_STATION =
            ModContent.BLOCK_ENTITIES.register("medical_station", () ->
                    BlockEntityType.Builder.of(MedicalStationBlockEntity::new, MedicalBlocks.MEDICAL_STATION.get())
                            .build(null));

    private MedicalBlockEntities() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
