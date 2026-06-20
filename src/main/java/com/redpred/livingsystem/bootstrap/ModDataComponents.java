package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 数据组件类型注册。用于医疗物品保存剂量、容量、过滤器余量、污染、血型兼容标识与设备资源等
 * （见开发文档 §31）。阶段一仅建立空注册表，具体组件在内容阶段加入。
 */
public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, LivingSystemMod.MOD_ID);

    private ModDataComponents() {
    }

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
