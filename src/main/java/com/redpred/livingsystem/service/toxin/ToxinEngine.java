package com.redpred.livingsystem.service.toxin;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.ExposureRoute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 毒素引擎（见开发文档 §5.8、§23）。管理毒素暴露实例的药代过程，并汇总全身毒素负荷。
 */
public interface ToxinEngine {

    /** 让玩家经指定途径接触一剂毒素（累加到未吸收量，无则创建暴露实例）。 */
    void expose(ServerPlayer player, ResourceLocation toxinId, ExposureRoute route, float dose);

    /** 推进玩家体内全部毒素的吸收、代谢与毒性更新，移除已清除者。 */
    void tick(ServerPlayer player, PlayerHealthData data);

    /** 汇总当前全身毒素负荷（全部活动毒素暴露的严重度之和，夹取 0~1）。 */
    float aggregateBurden(PlayerHealthData data);
}
