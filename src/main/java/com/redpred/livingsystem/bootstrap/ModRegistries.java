package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.content.item.MedicalItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创建模组所需的其他游戏注册项（创造模式物品栏等）。
 *
 * <p>预留为自定义 Codec 数据注册表、Data Map 等的注册入口；阶段一仅注册一个空的创造物品栏，
 * 物品将在后续阶段加入其中。</p>
 */
public final class ModRegistries {

    /** 创造模式物品栏注册表。 */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LivingSystemMod.MOD_ID);

    /** LivingSystem 医疗物品创造栏。 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.livingsystem"))
                    .icon(() -> new ItemStack(Items.POTION))
                    .displayItems((parameters, output) -> {
                        output.accept(MedicalItems.MEDICAL_SUPPLY.get());
                    })
                    .build());

    private ModRegistries() {
    }

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
