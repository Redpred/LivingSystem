package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.medication.MedicationRoute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link MedicationService} 默认实现。阶段一不施用也不推进任何药物。
 */
public final class DefaultMedicationService implements MedicationService {

    @Override
    public void administer(ServerPlayer player, ResourceLocation medicationId, MedicationRoute route, float dose) {
        // 阶段一占位
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        // 阶段一占位
    }
}
