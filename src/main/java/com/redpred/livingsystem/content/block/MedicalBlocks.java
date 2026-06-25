package com.redpred.livingsystem.content.block;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 医疗方块注册（见开发文档 §16.2）。医疗工作站为带方块实体的储存设施。
 * 方块始终注册，功能关闭通过移除配方/禁用逻辑实现（§31）。
 */
public final class MedicalBlocks {

    /** 医疗工作站方块（储存设施，右键打开库存界面）。 */
    public static final DeferredBlock<MedicalStationBlock> MEDICAL_STATION =
            ModContent.BLOCKS.register("medical_station", () -> new MedicalStationBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(2.0F).requiresCorrectToolForDrops()));

    /** 上述方块对应的方块物品。 */
    public static final DeferredItem<BlockItem> MEDICAL_STATION_ITEM =
            ModContent.ITEMS.registerSimpleBlockItem(MEDICAL_STATION);

    private MedicalBlocks() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
