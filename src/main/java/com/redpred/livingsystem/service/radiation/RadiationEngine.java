package com.redpred.livingsystem.service.radiation;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.RadiationType;
import net.minecraft.server.level.ServerPlayer;

/**
 * 辐射引擎（见开发文档 §5.8、§23）。累积辐射剂量并随时间衰减，按阶段阈值产生生物效应，汇总辐射负担。
 */
public interface RadiationEngine {

    /** 使玩家受到一份指定类型的辐射剂量（按类型合并到同一暴露实例）。 */
    void irradiate(ServerPlayer player, RadiationType type, float dose);

    /** 推进玩家全部辐射暴露的剂量衰减与生物效应更新，移除已排清者。 */
    void tick(ServerPlayer player, PlayerHealthData data);

    /** 汇总当前全身辐射生物效应负担（全部活动辐射暴露严重度之和，夹取 0~1）。 */
    float aggregateBurden(PlayerHealthData data);
}
