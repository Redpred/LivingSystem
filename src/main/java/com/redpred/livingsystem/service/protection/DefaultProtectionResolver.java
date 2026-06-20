package com.redpred.livingsystem.service.protection;

import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.protection.ProtectionResult;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.context.ExposureContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * {@link ProtectionResolver} 默认实现。阶段一返回无防护的中性结果。
 */
public final class DefaultProtectionResolver implements ProtectionResolver {

    @Override
    public ProtectionResult resolveTraumaProtection(DamageContext context, BodyRegion bodyRegion, List<ItemStack> equippedItems) {
        return ProtectionResult.NONE;
    }

    @Override
    public ProtectionResult resolveExposureProtection(ExposureContext context, List<ItemStack> equippedItems) {
        return ProtectionResult.NONE;
    }
}
