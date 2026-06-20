package com.redpred.livingsystem.service.protection;

import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.protection.ProtectionResult;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.context.ExposureContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 统一计算 LivingSystem 专用防护结果，防止重复减伤（见开发文档 §11.8、§23）。
 *
 * <p>原版护甲减伤只计算一次；本服务只处理穿透、组织损伤分布、伤口生成概率与环境屏障。结果只计算一次，
 * 后续各伤势模块读取该结果，不得分别重新计算装备防护。</p>
 */
public interface ProtectionResolver {

    /** 计算一次局部攻击的防护结果。 */
    ProtectionResult resolveTraumaProtection(DamageContext context, BodyRegion bodyRegion, List<ItemStack> equippedItems);

    /** 计算一次环境暴露的屏障防护结果。 */
    ProtectionResult resolveExposureProtection(ExposureContext context, List<ItemStack> equippedItems);
}
