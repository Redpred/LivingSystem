package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组内容注册聚合：物品、方块、方块实体与菜单类型。
 *
 * <p>所有内容始终注册，功能关闭时通过移除配方、禁用机器逻辑实现，不动态注销（见开发文档 §31）。
 * 阶段一仅建立空的 {@link DeferredRegister}，具体内容在 {@code content} 包中逐步登记。</p>
 */
public final class ModContent {

    /** 物品注册表。 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LivingSystemMod.MOD_ID);
    /** 方块注册表。 */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LivingSystemMod.MOD_ID);
    /** 方块实体类型注册表。 */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LivingSystemMod.MOD_ID);
    /** 菜单类型注册表。 */
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, LivingSystemMod.MOD_ID);

    private ModContent() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
    }
}
