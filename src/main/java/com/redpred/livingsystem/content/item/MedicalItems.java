package com.redpred.livingsystem.content.item;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 医疗物品注册（见开发文档 §16.1）。阶段一仅注册一个代表性占位物品以验证注册管线，
 * 完整医疗物品集合（绷带、止血带、夹板、药物、检查工具等）在后续阶段填充。
 */
public final class MedicalItems {

    /** 代表性占位医疗物资。 */
    public static final DeferredItem<Item> MEDICAL_SUPPLY =
            ModContent.ITEMS.registerSimpleItem("medical_supply", new Item.Properties());

    private MedicalItems() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
