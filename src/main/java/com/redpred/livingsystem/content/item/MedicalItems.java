package com.redpred.livingsystem.content.item;

import com.redpred.livingsystem.bootstrap.ModContent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 医疗物品注册（见开发文档 §16.1）。
 *
 * <p>阶段三注册一组基础创伤治疗物资；每个物品的 ID 与同名治疗定义
 * （{@code data/livingsystem/treatment_definition/<id>.json}）对应，右键自身或对队友使用即触发对应治疗。
 * 完整医疗设备、药物、检查工具与生产链在后续阶段填充。</p>
 */
public final class MedicalItems {

    /** 代表性占位医疗物资（保留，作为通用材料）。 */
    public static final DeferredItem<Item> MEDICAL_SUPPLY =
            ModContent.ITEMS.registerSimpleItem("medical_supply", new Item.Properties());

    /** 绷带：覆盖伤口、轻度止血并促进凝血。 */
    public static final DeferredItem<Item> BANDAGE =
            ModContent.ITEMS.registerSimpleItem("bandage", new Item.Properties().stacksTo(16));

    /** 止血纱布：强力外出血控制与加速凝血。 */
    public static final DeferredItem<Item> HEMOSTATIC_GAUZE =
            ModContent.ITEMS.registerSimpleItem("hemostatic_gauze", new Item.Properties().stacksTo(16));

    /** 止血带：快速控制肢体大出血。 */
    public static final DeferredItem<Item> TOURNIQUET =
            ModContent.ITEMS.registerSimpleItem("tourniquet", new Item.Properties().stacksTo(8));

    /** 夹板：稳定骨折、降低不稳定与移位。 */
    public static final DeferredItem<Item> SPLINT =
            ModContent.ITEMS.registerSimpleItem("splint", new Item.Properties().stacksTo(8));

    /** 缝合包：闭合开放伤口，显著降低再出血。 */
    public static final DeferredItem<Item> SUTURE_KIT =
            ModContent.ITEMS.registerSimpleItem("suture_kit", new Item.Properties().stacksTo(8));

    /** 消毒剂：清洁伤口、降低污染。 */
    public static final DeferredItem<Item> ANTISEPTIC =
            ModContent.ITEMS.registerSimpleItem("antiseptic", new Item.Properties().stacksTo(16));

    /** 止痛药：口服后逐步吸收，提供一段时间的镇痛（降低总疼痛）。 */
    public static final DeferredItem<Item> PAINKILLER =
            ModContent.ITEMS.registerSimpleItem("painkiller", new Item.Properties().stacksTo(16)
                    .food(new FoodProperties.Builder().alwaysEdible().build()));

    /** 防毒面具：佩戴（头部槽）后过滤毒气/烟雾，显著降低呼吸类环境暴露。 */
    public static final DeferredItem<net.minecraft.world.item.ArmorItem> GAS_MASK =
            ModContent.ITEMS.registerItem("gas_mask",
                    properties -> new net.minecraft.world.item.ArmorItem(
                            net.minecraft.world.item.ArmorMaterials.IRON,
                            net.minecraft.world.item.ArmorItem.Type.HELMET, properties));

    private MedicalItems() {
    }

    /** 触发类加载以登记静态条目。 */
    public static void init() {
    }
}
