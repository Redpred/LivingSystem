package com.redpred.livingsystem.content.block;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 医疗方块注册（见开发文档 §16.2）。阶段一仅注册一个代表性占位方块及其物品以验证注册管线，
 * 完整医疗设备/储存设施在后续阶段填充。方块始终注册，功能关闭通过移除配方/禁用逻辑实现（§31）。
 */
public final class MedicalBlocks {

    /** 代表性占位医疗工作站方块。 */
    public static final DeferredBlock<Block> MEDICAL_STATION =
            ModContent.BLOCKS.registerSimpleBlock("medical_station", BlockBehaviour.Properties.of());

    /** 上述方块对应的方块物品。 */
    public static final DeferredItem<BlockItem> MEDICAL_STATION_ITEM =
            ModContent.ITEMS.registerSimpleBlockItem(MEDICAL_STATION);

    private MedicalBlocks() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
