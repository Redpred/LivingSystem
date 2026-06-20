package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.medication.MedicationRoute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 药物服务（见开发文档 §13.8、§24.4）。处理药物吸收、作用与代谢；药物创建持续作用实例，而非立即改最终状态。
 */
public interface MedicationService {

    /** 给玩家施用一剂药物。 */
    void administer(ServerPlayer player, ResourceLocation medicationId, MedicationRoute route, float dose);

    /** 推进玩家体内全部药物的药代过程。 */
    void tick(ServerPlayer player, PlayerHealthData data);
}
