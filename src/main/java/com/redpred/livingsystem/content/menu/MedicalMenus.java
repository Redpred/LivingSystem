package com.redpred.livingsystem.content.menu;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 菜单类型注册（见开发文档 §16.2）。医疗工作站菜单使用带 {@code RegistryFriendlyByteBuf} 的工厂，
 * 以便客户端按方块位置打开。
 */
public final class MedicalMenus {

    public static final DeferredHolder<MenuType<?>, MenuType<MedicalStationMenu>> MEDICAL_STATION =
            ModContent.MENUS.register("medical_station", () ->
                    IMenuTypeExtension.create(MedicalStationMenu::new));

    private MedicalMenus() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
