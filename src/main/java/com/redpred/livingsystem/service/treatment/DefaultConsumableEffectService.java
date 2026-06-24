package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.medication.MedicationRoute;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.service.LivingServices;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/**
 * {@link ConsumableEffectService} 默认实现（阶段三 3.3）。
 *
 * <p>在物品成功完成食用/饮用后追加 LivingSystem 的系统性效果，不接管其他模组的右键逻辑：止痛药口服后
 * 转交药物服务进入药代；其它含食物属性的摄入物按营养补充代谢能量、营养与水分（原版饥饿已被屏蔽，
 * 这些资源是 LivingSystem 的权威能量来源）。普通摄入物只做系统性安全操作，不执行局部手术。</p>
 */
public final class DefaultConsumableEffectService implements ConsumableEffectService {

    /** 这些资源的展示上限（与 PhysiologyState 默认初值一致）。 */
    private static final float RESOURCE_CAP = 100.0F;

    @Override
    public void applyConsumable(ServerPlayer player, ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (DefaultMedicationService.PAINKILLER.equals(itemId)) {
            LivingServices.MEDICATION.administer(player, DefaultMedicationService.PAINKILLER, MedicationRoute.ORAL, 1.0F);
            return;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) {
            return;
        }
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        PhysiologyState p = data.physiology();
        float nutrition = food.nutrition();
        float saturation = food.saturation();
        p.setMetabolicEnergy(Mth.clamp(p.getMetabolicEnergy() + nutrition * 4.0F + saturation * 2.0F, 0.0F, RESOURCE_CAP));
        p.setNutrition(Mth.clamp(p.getNutrition() + nutrition * 3.0F, 0.0F, RESOURCE_CAP));
        p.setHydration(Mth.clamp(p.getHydration() + nutrition * 2.0F, 0.0F, RESOURCE_CAP));
    }
}
