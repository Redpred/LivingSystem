package com.redpred.livingsystem.content.menu;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 菜单类型注册（见开发文档 §16.2）。阶段一注册一个代表性占位类型。
 */
public final class MedicalMenus {

    public static final DeferredHolder<MenuType<?>, MenuType<MedicalStationMenu>> MEDICAL_STATION =
            ModContent.MENUS.register("medical_station", () ->
                    new MenuType<>(MedicalStationMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private MedicalMenus() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
