package com.redpred.livingsystem.service.protection;

import com.redpred.livingsystem.data.ProtectionProfileReloadListener;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.protection.ProtectionResult;
import com.redpred.livingsystem.rule.definition.ProtectionProfile;
import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.context.ExposureContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link ProtectionResolver} 默认实现（阶段四 4.3）。
 *
 * <p>按穿戴物品查数据驱动的 {@link ProtectionProfile}，把命中（且覆盖目标部位）的各件装备防护以"透过率连乘"
 * 方式合并为唯一 {@link ProtectionResult}：创伤减免穿透严重度/结构损伤/伤口生成，环境减免呼吸暴露/隔热/辐射。
 * 结果只计算一次，供伤势工厂与暴露采样读取，不在各模块重复计算（见 §11.8）。</p>
 */
public final class DefaultProtectionResolver implements ProtectionResolver {

    @Override
    public ProtectionResult resolveTraumaProtection(DamageContext context, @Nullable BodyRegion bodyRegion, List<ItemStack> equippedItems) {
        float penetration = 1.0F;
        float structure = 1.0F;
        float wound = 1.0F;
        float wear = 0.0F;
        for (ItemStack stack : equippedItems) {
            ProtectionProfile profile = match(stack, bodyRegion);
            if (profile == null) {
                continue;
            }
            penetration *= 1.0F - clamp01(profile.penetrationResist());
            structure *= 1.0F - clamp01(profile.structureProtection());
            wound *= 1.0F - clamp01(profile.woundChanceReduction());
            wear += 0.01F;
        }
        return new ProtectionResult(penetration, structure, wound, 1.0F, 1.0F, 1.0F, wear);
    }

    @Override
    public ProtectionResult resolveExposureProtection(ExposureContext context, List<ItemStack> equippedItems) {
        float respiratory = 1.0F;
        float dermal = 1.0F;
        float radiation = 1.0F;
        float wear = 0.0F;
        for (ItemStack stack : equippedItems) {
            ProtectionProfile profile = match(stack, null);
            if (profile == null) {
                continue;
            }
            respiratory *= 1.0F - clamp01(profile.respiratoryFiltration());
            dermal *= 1.0F - clamp01(profile.thermalInsulation());
            radiation *= 1.0F - clamp01(profile.radiationShielding());
            wear += 0.005F;
        }
        return new ProtectionResult(1.0F, 1.0F, 1.0F, respiratory, dermal, radiation, wear);
    }

    /** 物品命中防护定义且（指定部位时）覆盖该部位才生效；全身定义（覆盖列表为空）始终生效。 */
    @Nullable
    private static ProtectionProfile match(ItemStack stack, @Nullable BodyRegion bodyRegion) {
        if (stack.isEmpty()) {
            return null;
        }
        ProtectionProfile profile = ProtectionProfileReloadListener.forItem(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        if (profile == null) {
            return null;
        }
        if (bodyRegion != null && !profile.coveredRegions().isEmpty() && !profile.coveredRegions().contains(bodyRegion)) {
            return null;
        }
        return profile;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
